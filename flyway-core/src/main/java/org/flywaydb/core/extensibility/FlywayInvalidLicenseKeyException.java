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
package org.flywaydb.core.extensibility;

import org.flywaydb.core.internal.license.FlywayLicensingException;

public class FlywayInvalidLicenseKeyException extends FlywayLicensingException {
    public FlywayInvalidLicenseKeyException() {
        super("Invalid license key. Ensure flyway.licenseKey is set to a valid Flyway license key" +
                      " (\"FL01\" followed by 512 hex chars)");
    }

    public FlywayInvalidLicenseKeyException(String message, Exception e) {
        super(message, e);
    }
}
