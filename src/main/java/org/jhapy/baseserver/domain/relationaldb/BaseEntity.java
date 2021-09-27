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
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-06
 */
@Data
@EqualsAndHashCode(
    exclude = {"createdBy", "modifiedBy", "created", "modified", "version", "client"})
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  /** Who create this record (no ID, use username) */
  @DiffIgnore @CreatedBy private String createdBy;

  /** When this record has been created */
  @DiffIgnore @CreatedDate private Instant created;

  /** How did the last modification of this record (no ID, use username) */
  @DiffIgnore @LastModifiedBy private String modifiedBy;

  /** When this record was last updated */
  @DiffIgnore @LastModifiedDate private Instant modified;

  /** Version of the record. Used for synchronization and concurrent access. */
  @DiffIgnore @Version private Long version;

  /** Indicate if the current record is active (deactivate instead of delete) */
  private Boolean isActive = Boolean.TRUE;

  private Long externalClientId;

  @Transient private Boolean isSkipAfterSave = Boolean.FALSE;

  private String syncId;

  /*
  @JoinColumn(name = "org_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Org          org;
   */
}