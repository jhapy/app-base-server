/*
 * Copyright 2020-2020 the original author or authors from the JHapy project.
 *
 * This file is part of the JHapy project, see https://www.jhapy.org/ for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jhapy.baseserver.utils;

import java.util.HashMap;
import java.util.Map;
import org.jhapy.baseserver.domain.graphdb.EntityTranslation;
import org.jhapy.baseserver.domain.graphdb.EntityTranslations;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.data.neo4j.core.convert.Neo4jConversionService;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyConverter;
import org.springframework.data.neo4j.core.convert.Neo4jPersistentPropertyToMapConverter;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 10/28/19
 */
public abstract class DefaultTranslationConverter implements
    Neo4jPersistentPropertyToMapConverter<String, EntityTranslations> {

  private final String prefix;

  public DefaultTranslationConverter(String prefix) {
    this.prefix = prefix;
  }

    @Override
    public Map<String, Value> decompose(EntityTranslations translations, Neo4jConversionService neo4jConversionService) {
        Map<String, Value> result = new HashMap<>();
        Map<String, EntityTranslation> entityValue = translations.getTranslations();
        if (entityValue != null) {
            entityValue.keySet().forEach(key -> {
                EntityTranslation t = entityValue.get(key);
                result.put(key + ".value", Values.value(t.getValue()));
                result.put(key + ".isTranslated", Values.value(t.getIsTranslated()));
                result.put(key + ".isDefault", Values.value(t.getIsDefault()));
            });
        }
        return result;
    }

    @Override
    public EntityTranslations compose(Map<String, Value> source, Neo4jConversionService neo4jConversionService) {
        Map<String, EntityTranslation> result = new HashMap<>();
        source.keySet().forEach(key -> {
            String[] vals = key.split("\\.");
            if (vals.length == 2) {
                String iso3Language = vals[0];

                EntityTranslation translation;
                if (result.containsKey(iso3Language)) {
                    translation = result.get(iso3Language);
                } else {
                    translation = new EntityTranslation();
                    translation.setIso3Language(iso3Language);
                    result.put(iso3Language, translation);
                }
                switch (vals[1]) {
                    case "value":
                        translation.setValue(source.get(key).asString());
                        break;
                    case "isTranslated":
                        translation
                            .setIsTranslated(source.get(key).asBoolean());
                        break;
                    case "isDefault":
                        translation
                            .setIsDefault(source.get(key).asBoolean());
                        break;
                }
            }
        });
        return new EntityTranslations(result);
    }
}
