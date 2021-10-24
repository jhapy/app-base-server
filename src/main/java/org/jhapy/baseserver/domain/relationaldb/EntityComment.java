package org.jhapy.baseserver.domain.relationaldb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(indexes = @Index(columnList = "relatedEntityId, relatedEntityName"))
public class EntityComment extends BaseEntity {
  @Column(nullable = false)
  private String topic;

  private String text;

  @Column(nullable = false)
  private UUID relatedEntityId;

  @Column(nullable = false)
  private String relatedEntityName;

  private String authorId;

  private String authorNickname;
}