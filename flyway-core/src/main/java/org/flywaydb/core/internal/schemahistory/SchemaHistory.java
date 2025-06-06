/*-
 * ========================LICENSE_START=================================
 * flyway-core
 * ========================================================================
 * Copyright (C) 2010 - 2025 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.core.internal.schemahistory;

import lombok.experimental.ExtensionMethod;
import org.flywaydb.core.api.CoreMigrationType;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationPattern;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.output.RepairResult;
import org.flywaydb.core.api.resolver.ResolvedMigration;
import org.flywaydb.core.extensibility.AppliedMigration;
import org.flywaydb.core.extensibility.MigrationType;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.util.AbbreviationUtils;
import org.flywaydb.core.internal.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * The schema history used to track all applied migrations.
 */
@ExtensionMethod(Arrays.class)
public abstract class SchemaHistory {
    public static final String NO_DESCRIPTION_MARKER = "<< no description >>";

    /**
     * The schema history table used by Flyway.
     * Non-final due to the table name fallback mechanism. Will be made final in Flyway 6.0.
     */
    protected Table table;

    /**
     * Acquires an exclusive read-write lock on the schema history table. This lock will be released automatically upon completion.
     *
     * @return The result of the action.
     */
    public abstract <T> T lock(Callable<T> callable);

    /**
     * @return Whether the schema history table exists.
     */
    public abstract boolean exists();

    /**
     * Creates the schema history. Do nothing if it already exists.
     *
     * @param baseline Whether to include the creation of a baseline marker.
     */
    public abstract void create(boolean baseline);

    /**
     * Drops the schema history table
     */
    public void drop() {
        throw new FlywayException("Dropping the schema history table is not supported for this SchemaHistory implementation");
    }

    /**
     * Checks whether the schema history table contains at least one non-synthetic applied migration.
     *
     * @return {@code true} if it does, {@code false} if it doesn't.
     */
    public final boolean hasNonSyntheticAppliedMigrations() {
        for (AppliedMigration appliedMigration : allAppliedMigrations()) {
            if (!appliedMigration.getType().isSynthetic()
                    && !appliedMigration.getType().isUndo()
            ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return The list of all migrations applied on the schema in the order they were applied (oldest first).
     * An empty list if no migration has been applied so far.
     */
    public abstract List<AppliedMigration> allAppliedMigrations();

    /**
     * Retrieves the baseline marker from the schema history table.
     *
     * @return The baseline marker or {@code null} if none could be found.
     */
    public final AppliedMigration getBaselineMarker() {
        List<AppliedMigration> appliedMigrations = allAppliedMigrations();
        // BASELINE can only be the first or second (in case there is a SCHEMA one) migration.
        for (int i = 0; i < Math.min(appliedMigrations.size(), 2); i++) {
            AppliedMigration appliedMigration = appliedMigrations.get(i);
            if (appliedMigration.getType() == CoreMigrationType.BASELINE) {
                return appliedMigration;
            }
        }
        return null;
    }

    /**
     * <p>
     * Repairs the schema history table after a failed migration.
     * This is only necessary for databases without DDL-transaction support.
     * </p>
     * <p>
     * On databases with DDL transaction support, a migration failure automatically triggers a rollback of all changes,
     * including the ones in the schema history table.
     * </p>
     *
     * @param repairResult The result object containing which failed migrations were removed.
     * @param migrationPatternFilter The migration patterns to filter by.
     */
    public abstract boolean removeFailedMigrations(RepairResult repairResult, MigrationPattern[] migrationPatternFilter);

    /**
     * Indicates in the schema history table that Flyway created these schemas.
     *
     * @param schemas The schemas that were created by Flyway.
     */
    public final void addSchemasMarker(Schema[] schemas) {
        addAppliedMigration(null, "<< Flyway Schema Creation >>",
                            CoreMigrationType.SCHEMA, StringUtils.arrayToCommaDelimitedString(schemas), null, 0, true);
    }

    /**
     * Checks whether the schema history table contains a marker row for schema creation.
     *
     * @return {@code true} if it does, {@code false} if it doesn't.
     */
    public final boolean hasSchemasMarker() {
        final List<AppliedMigration> appliedMigrations = allAppliedMigrations();
        return !appliedMigrations.isEmpty() && appliedMigrations.stream().anyMatch(x -> x.getType() == CoreMigrationType.SCHEMA);
    }

    public List<String> getSchemasCreatedByFlyway() {
        if (!hasSchemasMarker()) {
            return new ArrayList<>();
        }

        return allAppliedMigrations().stream()
                .filter(x -> x.getType() == CoreMigrationType.SCHEMA)
                .map(AppliedMigration::getScript)
                .flatMap(script -> Arrays.stream(script.split(",")))
                .map(result -> table.getDatabase().unQuote(result))
                .collect(Collectors.toList());
    }

    /**
     * Updates this applied migration to match this resolved migration.
     *
     * @param appliedMigration The applied migration to update.
     * @param resolvedMigration The resolved migration to source the new values from.
     */
    public abstract void update(AppliedMigration appliedMigration, ResolvedMigration resolvedMigration);

    /**
     * Update the schema history to mark this migration as DELETED
     *
     * @param appliedMigration The applied migration to mark as DELETED
     */
    public abstract void delete(AppliedMigration appliedMigration);

    /**
     * Clears the applied migration cache.
     */
    public void clearCache() {
        // Do nothing by default.
    }

    /**
     * Records a new applied migration.
     *
     * @param version The target version of this migration.
     * @param description The description of the migration.
     * @param type The type of migration (BASELINE, SQL, ...)
     * @param script The name of the script to execute for this migration, relative to its classpath location.
     * @param checksum The checksum of the migration. (Optional)
     * @param executionTime The execution time (in millis) of this migration.
     * @param success Flag indicating whether the migration was successful or not.
     */
    public final void addAppliedMigration(MigrationVersion version, String description, MigrationType type,
                                          String script, Integer checksum, int executionTime, boolean success) {
        int installedRank = calculateInstalledRank(type);
        doAddAppliedMigration(
                installedRank,
                version,
                AbbreviationUtils.abbreviateDescription(description),
                type,
                AbbreviationUtils.abbreviateScript(script),
                checksum,
                executionTime,
                success);
    }

    /**
     * Calculates the installed rank for the new migration to be inserted.
     *
     * @param type The type of migration (SCHEMA, SQL, ...)
     * @return The installed rank.
     */
    protected int calculateInstalledRank(MigrationType type) {
        List<AppliedMigration> appliedMigrations = allAppliedMigrations();
        if (appliedMigrations.isEmpty()) {
            return type == CoreMigrationType.SCHEMA ? 0 : 1;
        }
        return appliedMigrations.get(appliedMigrations.size() - 1).getInstalledRank() + 1;
    }

    protected abstract void doAddAppliedMigration(int installedRank, MigrationVersion version, String description,
                                                  MigrationType type, String script, Integer checksum,
                                                  int executionTime, boolean success);

    @Override
    public String toString() {
        return table.toString();
    }
}
