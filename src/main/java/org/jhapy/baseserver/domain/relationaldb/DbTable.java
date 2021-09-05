package org.jhapy.baseserver.domain.relationaldb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class DbTable extends BaseEntity {
  private String name;

  private String description;

  private String entityName;

  private String tableName;

  private Boolean isDeletable;

  private Boolean isChangeLog;

  private Boolean isSecurityEnabled;

  @Enumerated(EnumType.STRING)
  private AccessLevelEnum accessLevel;
}