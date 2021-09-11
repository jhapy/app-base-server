package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.EntityComment;

import java.time.Instant;
import java.util.List;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 02/08/2020
 */
public interface EntityCommentService extends CrudRelationalService<EntityComment> {

  List<EntityComment> getEntityComments(
      Long relatedEntityId, String relatedEntityName, Instant since);
}