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
package org.flywaydb.core.api.callback;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

/**
 * The Flyway lifecycle events that can be handled in callbacks.
 */
@RequiredArgsConstructor
public enum Event {
    /**
     * Fired before clean is executed. This event will be fired in a separate transaction from the actual clean operation.
     */
    BEFORE_CLEAN("beforeClean"),
    /**
     * Fired after clean has succeeded. This event will be fired in a separate transaction from the actual clean operation.
     */
    AFTER_CLEAN("afterClean"),
    /**
     * Fired after clean has failed. This event will be fired in a separate transaction from the actual clean operation.
     */
    AFTER_CLEAN_ERROR("afterCleanError"),

    /**
     * Fired before migrate is executed. This event will be fired in a separate transaction from the actual migrate operation.
     */
    BEFORE_MIGRATE("beforeMigrate"),
    /**
     * Fired before each individual migration is executed. This event will be fired within the same transaction (if any)
     * as the migration and can be used for things like setting up connection parameters that are required by migrations.
     */
    BEFORE_EACH_MIGRATE("beforeEachMigrate"),
    /**
     * Fired before each individual statement in a migration is executed. This event will be fired within the same transaction (if any)
     * as the migration and can be used for things like asserting a statement complies with policy (for example: no grant statements allowed).
     */
    BEFORE_EACH_MIGRATE_STATEMENT("beforeEachMigrateStatement"),
    /**
     * Fired after each individual statement in a migration that succeeded. This event will be fired within the same transaction (if any)
     * as the migration.
     */
    AFTER_EACH_MIGRATE_STATEMENT("afterEachMigrateStatement"),
    /**
     * Fired after each individual statement in a migration that failed. This event will be fired within the same transaction (if any)
     * as the migration.
     */
    AFTER_EACH_MIGRATE_STATEMENT_ERROR("afterEachMigrateStatementError"),
    /**
     * Fired after each individual migration that succeeded. This event will be fired within the same transaction (if any)
     * as the migration.
     */
    AFTER_EACH_MIGRATE("afterEachMigrate"),
    /**
     * Fired after each individual migration that failed. This event will be fired within the same transaction (if any)
     * as the migration.
     */
    AFTER_EACH_MIGRATE_ERROR("afterEachMigrateError"),
    /**
     * Fired before any repeatable migrations are applied. This event will be fired in a separate transaction from the actual migrate operation.
     */
    BEFORE_REPEATABLES("beforeRepeatables"),
    /**
     * Fired after all versioned migrations are applied. This event will be fired in a separate transaction from the actual migrate operation.
     */
    AFTER_VERSIONED("afterVersioned"),
    /**
     * Fired after migrate has succeeded, and at least one migration has been applied. This event will be fired in a separate transaction from the actual migrate operation.
     */
    AFTER_MIGRATE_APPLIED("afterMigrateApplied"),
    /**
     * Fired after migrate has succeeded. This event will be fired in a separate transaction from the actual migrate operation.
     */
    AFTER_MIGRATE("afterMigrate"),
    /**
     * Fired after migrate has failed. This event will be fired in a separate transaction from the actual migrate operation.
     */
    AFTER_MIGRATE_ERROR("afterMigrateError"),

    /**
     * Fired before undo is executed. This event will be fired in a separate transaction from the actual undo operation.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    BEFORE_UNDO("beforeUndo"),
    /**
     * Fired before each individual undo is executed. This event will be fired within the same transaction (if any)
     * as the undo and can be used for things like setting up connection parameters that are required by undo.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    BEFORE_EACH_UNDO("beforeEachUndo"),
    /**
     * Fired before each individual statement in an undo migration is executed. This event will be fired within the same transaction (if any)
     * as the migration and can be used for things like asserting a statement complies with policy (for example: no grant statements allowed).
     * <p><i>Flyway Teams Edition only</i></p>
     */
    BEFORE_EACH_UNDO_STATEMENT("beforeEachUndoStatement"),
    /**
     * Fired after each individual statement in an undo migration that succeeded. This event will be fired within the same transaction (if any)
     * as the migration.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_EACH_UNDO_STATEMENT("afterEachUndoStatement"),
    /**
     * Fired after each individual statement in an undo migration that failed. This event will be fired within the same transaction (if any)
     * as the migration.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_EACH_UNDO_STATEMENT_ERROR("afterEachUndoStatementError"),
    /**
     * Fired after each individual undo that succeeded. This event will be fired within the same transaction (if any)
     * as the undo.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_EACH_UNDO("afterEachUndo"),
    /**
     * Fired after each individual undo that failed. This event will be fired within the same transaction (if any)
     * as the undo.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_EACH_UNDO_ERROR("afterEachUndoError"),
    /**
     * Fired after undo has succeeded. This event will be fired in a separate transaction from the actual undo operation.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_UNDO("afterUndo"),
    /**
     * Fired after undo has failed. This event will be fired in a separate transaction from the actual undo operation.
     * <p><i>Flyway Teams Edition only</i></p>
     */
    AFTER_UNDO_ERROR("afterUndoError"),

    /**
     * Fired before validate is executed. This event will be fired in a separate transaction from the actual validate operation.
     */
    BEFORE_VALIDATE("beforeValidate"),
    /**
     * Fired after validate has succeeded. This event will be fired in a separate transaction from the actual validate operation.
     */
    AFTER_VALIDATE("afterValidate"),
    /**
     * Fired after validate has failed. This event will be fired in a separate transaction from the actual validate operation.
     */
    AFTER_VALIDATE_ERROR("afterValidateError"),

    /**
     * Fired before baseline is executed. This event will be fired in a separate transaction from the actual baseline operation.
     */
    BEFORE_BASELINE("beforeBaseline"),
    /**
     * Fired after baseline has succeeded. This event will be fired in a separate transaction from the actual baseline operation.
     */
    AFTER_BASELINE("afterBaseline"),
    /**
     * Fired after baseline has failed. This event will be fired in a separate transaction from the actual baseline operation.
     */
    AFTER_BASELINE_ERROR("afterBaselineError"),

    /**
     * Fired before repair is executed. This event will be fired in a separate transaction from the actual repair operation.
     */
    BEFORE_REPAIR("beforeRepair"),
    /**
     * Fired after repair has succeeded. This event will be fired in a separate transaction from the actual repair operation.
     */
    AFTER_REPAIR("afterRepair"),
    /**
     * Fired after repair has failed. This event will be fired in a separate transaction from the actual repair operation.
     */
    AFTER_REPAIR_ERROR("afterRepairError"),

    /**
     * Fired before info is executed. This event will be fired in a separate transaction from the actual info operation.
     */
    BEFORE_INFO("beforeInfo"),
    /**
     * Fired after info has succeeded. This event will be fired in a separate transaction from the actual info operation.
     */
    AFTER_INFO("afterInfo"),
    /**
     * Fired after info has failed. This event will be fired in a separate transaction from the actual info operation.
     */
    AFTER_INFO_ERROR("afterInfoError"),
    /**
     * Fired after a migrate operation has finished.
     */
    AFTER_MIGRATE_OPERATION_FINISH("afterMigrateOperationFinish"),
    /**
     * Fired after an info operation has finished.
     */
    AFTER_INFO_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Fired after a clean operation has finished.
     */
    AFTER_CLEAN_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Fired after a validate operation has finished.
     */
    AFTER_VALIDATE_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Fired after a validate operation has finished.
     */
    AFTER_UNDO_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Fired after a validate operation has finished.
     */
    AFTER_REPAIR_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Fired after a validate operation has finished.
     */
    AFTER_BASELINE_OPERATION_FINISH("afterInfoOperationFinish"),
    /**
     * Deprecated. Fired before any non-existent schemas are created.
     */
    CREATE_SCHEMA("createSchema"),
    /**
     * Fired before any non-existent schemas are created.
     */
    BEFORE_CREATE_SCHEMA("beforeCreateSchema"),
    /**
     * Fired before a connection is created. These must be arbitrary scripts only (e.g. ps1, cmd, sh etc.)
     * <p><i>Flyway Teams Edition only</i></p>
     */
    BEFORE_CONNECT("beforeConnect"),
    AFTER_CONNECT("afterConnect");

    /**
     * @return The id of an event. Examples: {@code beforeClean}, {@code afterEachMigrate}, ...
     */
    @Getter
    private final String id;

    /**
     * Retrieves the event with this id.
     *
     * @param id The id.
     * @return The event. {@code null} if not found.
     */
    public static Event fromId(String id) {
        for (Event event : values()) {
            if (event.id.equals(id)) {
                return event;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return id;
    }
}
