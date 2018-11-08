/*
 * Copyright 2018 the original author or authors.
 *
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
 */

package io.r2dbc.mssql.codec;

import io.r2dbc.mssql.message.type.TypeInformation;

/**
 * Interface declaring metadata to allow decoding of a related value.
 *
 * @author Mark Paluch
 */
public interface Decodable {

    /**
     * Returns the type that is associated with the decodable value.
     *
     * @return the type that is associated with the decodable value.
     */
    TypeInformation getType();

    /**
     * Returns the name of the decodable item. This is typically a parameter name or a column name.
     *
     * @return the name of the decodable.
     */
    String getName();
}