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

package org.jhapy.baseserver.domain.graphdb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Base class for all translations
 *
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-12
 */
@Data
public class EntityTranslations implements Serializable {

  private Map<String, EntityTranslation> translations;

  public EntityTranslations() {
    this.translations = new HashMap<>();
  }

  public EntityTranslations(Map<String, EntityTranslation> translations) {
    this.translations = translations;
  }

  public String getTranslation(String iso3Language) {
    if (translations.containsKey(iso3Language)) {
      return translations.get(iso3Language).getValue();
    } else {
      return getDefaultTranslation();
    }
  }

  public String getDefaultTranslation() {
    for (EntityTranslation entityTranslation : translations.values()) {
      if (entityTranslation.isDefault()) {
        return entityTranslation.getValue();
      }
    }

    if (translations.size() > 0) {
      return translations.values().iterator().next().getValue();
    } else {
      return null;
    }
  }

  public void setTranslation(String iso3Language, String value, Boolean isDefault) {
    if (translations.containsKey(iso3Language)) {
      translations.get(iso3Language).setValue(value);
    } else {
      var entityTranslation = new EntityTranslation();
      entityTranslation.setIso3Language(iso3Language);
      entityTranslation.setDefault(isDefault);
      entityTranslation.setValue(value);
      entityTranslation.setTranslated(true);

      translations.put(iso3Language, entityTranslation);
    }
  }

  public void clearTranslation(String iso3Language) {
    if (translations.containsKey(iso3Language)) {
      translations.remove(iso3Language);
    }
  }

  public void setTranslation(String iso3Language, String value) {
    setTranslation(iso3Language, value, translations.size() == 0);
  }

  public void clearTranslations() {
    translations = new HashMap<>();
  }

  public void addTranslation(EntityTranslation entityTranslation) {
    translations.put(entityTranslation.getIso3Language(), entityTranslation);
  }
}
