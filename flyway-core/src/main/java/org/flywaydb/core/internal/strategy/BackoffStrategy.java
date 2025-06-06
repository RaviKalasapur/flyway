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
package org.flywaydb.core.internal.strategy;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BackoffStrategy {

    private int current;
    private final int exponent;
    private final int interval;

    /**
     * @return The current value of the counter and immediately updates it with the next value
     */
    public int next() {
        int temp = current;
        current = Math.min(current * exponent, interval);
        return temp;
    }

    /**
     * @return The current value of the counter without updating it
     */
    public int peek() {
        return current;
    }
}
