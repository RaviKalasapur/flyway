/*-
 * ========================LICENSE_START=================================
 * flyway-database-hsqldb
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
package org.flywaydb.database.hsqldb;

import lombok.CustomLog;
import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.SQLException;

/**
 * HSQLDB-specific table.
 */
@CustomLog
public class HSQLDBTable extends Table<HSQLDBDatabase, HSQLDBSchema> {

    /**
     * Creates a new Hsql table.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param database The database-specific support.
     * @param schema The schema this table lives in.
     * @param name The name of the table.
     */
    HSQLDBTable(JdbcTemplate jdbcTemplate, HSQLDBDatabase database, HSQLDBSchema schema, String name) {
        super(jdbcTemplate, database, schema, name);
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.execute("DROP TABLE " + database.quote(schema.getName(), name) + " CASCADE");
    }

    @Override
    protected boolean doExists() throws SQLException {
        return exists(null, schema, name);
    }

    @Override
    protected void doLock() throws SQLException {
        if (!database.getVersion().isAtLeast("2")) {
            LOG.debug("Unable to lock " + this + " as Hsql 1.8 does not support locking. No concurrent migration supported.");
            return;
        }
        jdbcTemplate.execute("LOCK TABLE " + this + " WRITE");
    }
}
