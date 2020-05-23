package org.jhapy.baseserver.domain.graphdb;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NodeEntity
public abstract class BaseEntityWithComments extends BaseEntity {

  @Relationship("HAS_COMMENT")
  private List<Comment> comments = new ArrayList<>();
}
