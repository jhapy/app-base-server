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

package org.jhapy.baseserver.domain.relationaldb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;

/**
 * Base class for all translations
 *
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-09-29
 */
@Data
@EqualsAndHashCode(exclude = {"createdBy", "modifiedBy", "created", "modified", "version"})
@ToString
@MappedSuperclass
public abstract class EntityTranslationV2 implements Serializable {

  private Boolean isDefault;

  private Boolean isTranslated;

  @Transient
  private String iso3Language;

  @Transient
  private Long relatedEntityId;

  /**
   * Who create this record (no ID, use username)
   */
  @DiffIgnore
  @CreatedBy
  private String createdBy;

  /**
   * When this record has been created
   */
  @DiffIgnore
  @CreatedDate
  private Instant created;

  /**
   * How did the last modification of this record (no ID, use username)
   */
  @DiffIgnore
  @LastModifiedBy
  private String modifiedBy;

  /**
   * When this record was last updated
   */
  @DiffIgnore
  @LastModifiedDate
  private Instant modified;
}