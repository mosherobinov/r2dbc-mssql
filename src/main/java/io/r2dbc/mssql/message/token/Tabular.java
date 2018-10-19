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
package io.r2dbc.mssql.message.token;

import io.netty.buffer.ByteBuf;
import io.r2dbc.mssql.message.Message;
import io.r2dbc.mssql.message.header.Header;
import io.r2dbc.mssql.message.header.Type;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tabular response.
 * 
 * @author Mark Paluch
 * @see Type#TABULAR_RESULT
 */
public final class Tabular implements Message {

	private final List<? extends DataToken> tokens;

	public Tabular(List<? extends DataToken> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Decode the {@link Prelogin} response from a {@link ByteBuf}.
	 * 
	 * @param header must not be null.
	 * @param byteBuf must not be null.
	 * @return the decoded {@link Tabular} response {@link Message}.
	 */
	public static Tabular decode(Header header, ByteBuf byteBuf) {

		Objects.requireNonNull(header, "Header must not be null");
		Objects.requireNonNull(byteBuf, "ByteBuf must not be null");

		List<DataToken> tokens = new ArrayList<>();

		while (true) {

			byte type = Decode.asByte(byteBuf);

			if (type == (byte) 0xFF) {
				break;
			}

			if (type == EnvChangeToken.TYPE) {
				tokens.add(EnvChangeToken.decode(byteBuf));
			}

			if (type == InfoToken.TYPE) {
				tokens.add(InfoToken.decode(byteBuf));
			}

			if (type == LoginAckToken.TYPE) {
				tokens.add(LoginAckToken.decode(byteBuf));
			}
		}

		return new Tabular(tokens);
	}

	/**
	 * @return the tokens.
	 */
	public List<? extends DataToken> getTokens() {
		return this.tokens;
	}

	/**
	 * Resolve a {@link Prelogin.Token} given its {@link Class type}.
	 * 
	 * @param tokenType
	 * @return
	 */
	@Nullable
	private DataToken findToken(Predicate<DataToken> filter) {

		Objects.requireNonNull(filter, "Filter must not be null");

		for (DataToken token : this.tokens) {
			if (filter.test(token)) {
				return token;
			}
		}

		return null;
	}

	/**
	 * Find a {@link DataToken} by its {@link Class type} and a {@link Predicate}.
	 * 
	 * @param tokenType
	 * @return
	 */
	public <T extends DataToken> Optional<T> getToken(Class<? extends T> tokenType) {

		Objects.requireNonNull(tokenType, "Token type must not be null");

		return Optional.ofNullable(findToken(tokenType::isInstance)).map(tokenType::cast);
	}

	/**
	 * Find a {@link DataToken} by its {@link Class type} and a {@link Predicate}.
	 * 
	 * @param tokenType
	 * @return
	 */
	public <T extends DataToken> Optional<T> getToken(Class<? extends T> tokenType, Predicate<T> filter) {

		Objects.requireNonNull(tokenType, "Token type must not be null");
		Objects.requireNonNull(filter, "Filter must not be null");

		Predicate<DataToken> predicate = tokenType::isInstance;
		return Optional.ofNullable(findToken(predicate.and(dataToken -> filter.test(tokenType.cast(dataToken)))))
				.map(tokenType::cast);
	}

	/**
	 * Find a {@link DataToken} by its {@link Class type} and a {@link Predicate}.
	 * 
	 * @param tokenType
	 * @return
	 * @throws IllegalArgumentException if no token was found.
	 */
	public <T extends DataToken> T getRequiredToken(Class<? extends T> tokenType) {

		return getToken(tokenType).orElseThrow(
				() -> new IllegalArgumentException(String.format("Token of type %s available", tokenType.getName())));
	}

	/**
	 * Find a {@link DataToken} by its {@link Class type} and a {@link Predicate}.
	 * 
	 * @param tokenType
	 * @param filter
	 * @return
	 * @throws IllegalArgumentException if no token was found.
	 */
	public <T extends DataToken> T getRequiredToken(Class<? extends T> tokenType, Predicate<T> filter) {

		return getToken(tokenType, filter).orElseThrow(
				() -> new IllegalArgumentException(String.format("Token of type %s available", tokenType.getName())));
	}

	/**
	 * Find a collection of {@link DataToken tokens} given their {@link Class type}.
	 * 
	 * @param tokenType the desired token type.
	 * @return List of tokens.
	 */
	public <T extends DataToken> List<T> getTokens(Class<T> tokenType) {

		Objects.requireNonNull(tokenType, "Token type must not be null");

		List<T> result = new ArrayList<>();

		for (DataToken token : this.tokens) {
			if (tokenType.isInstance(token)) {
				result.add(tokenType.cast(token));
			}
		}

		return result;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append(" [tokens=").append(this.tokens);
		sb.append(']');
		return sb.toString();
	}
}