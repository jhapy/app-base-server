/*
 * Copyright 2011-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jhapy.baseserver.utils;

import org.apiguardian.api.API;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverterFactory;
import org.springframework.data.neo4j.core.mapping.Neo4jPersistentProperty;

import java.lang.annotation.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Inherited
@ConvertWith(converterFactory = InstantStringConverterFactory.class)
public @interface InstantString {
}

final class InstantStringConverterFactory implements Neo4jPersistentPropertyConverterFactory {

	@Override
	public Neo4jPersistentPropertyConverter getPropertyConverterFor(Neo4jPersistentProperty persistentProperty) {

		if (persistentProperty.getActualType() == Instant.class) {
			InstantString config = persistentProperty.getRequiredAnnotation(InstantString.class);
			return new InstantStringConverter();
		} else {
			throw new UnsupportedOperationException(
					"Other types than java.util.Date are not yet supported. Please file a ticket.");
		}
	}
}

final class InstantStringConverter implements Neo4jPersistentPropertyConverter<Instant> {

	InstantStringConverter() {
	}

	@Override
	public Value write(Instant source) {
		return Values.value(source.toString());
	}

	@Override
	public Instant read(Value source) {
		return Instant.parse(source.asString());
	}

}