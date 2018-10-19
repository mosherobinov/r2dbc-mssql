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
package io.r2dbc.mssql.message.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * @author Mark Paluch
 */
final class TypeUnitTests {

	@Test
	void shouldResolveType() {
		assertThat(Type.valueOf((byte) 0x1)).isEqualTo(Type.SQL_BATCH);
	}

	@Test
	void typeResolutionShouldFail() {
		assertThatThrownBy(() -> Type.valueOf((byte) 0xFF)).hasMessageContaining("Invalid header type 0xFF");
	}
}