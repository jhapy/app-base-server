package org.jhapy.baseserver.repository.relationaldb;

import org.jhapy.baseserver.domain.relationaldb.EntityComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EntityCommentRepository extends BaseRepository<EntityComment> {
  @Query(
      "FROM EntityComment WHERE relatedEntityId = :relatedEntityId AND relatedEntityName = :relatedEntityName AND created >= :since")
  List<EntityComment> findByRelatedEntityIdAndRelatedEntityNameAfterSince(
      @Param("relatedEntityId") Long relatedEntityId,
      @Param("relatedEntityName") String relatedEntityName,
      @Param("since") Instant since);
}