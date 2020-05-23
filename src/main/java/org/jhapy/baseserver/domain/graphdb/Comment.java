package org.jhapy.baseserver.domain.graphdb;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.jhapy.dto.utils.StoredFile;

/**
 * A comment can be attached to anything
 *
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"relatedEntity", "parent"})
@NodeEntity
public class Comment extends BaseEntity {

  /**
   * Text of the Comment
   */
  private String content;

  /**
   * List of attachments attached to the comment
   */
  @org.springframework.data.annotation.Transient
  @org.neo4j.ogm.annotation.Transient
  private List<StoredFile> attachments = null;

  private List<String> attachmentIds = new ArrayList<>();

  /**
   * In case of a reply, this is the parent comment
   */
  @Relationship("HAS_PARENT")
  private Comment parent;

  @Relationship("HAS_RELATED_ENTITY")
  private BaseEntity relatedEntity;
}
