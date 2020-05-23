package org.jhapy.baseserver.repository.graphdb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.jhapy.baseserver.domain.graphdb.Comment;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-14
 */
public interface CommentRepository extends BaseRepository<Comment> {

  @Query(value =
      "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) "
          + "WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = {relatedEntityId} RETURN m ORDER BY m.created DESC",
      countQuery = "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) "
          + " WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = {relatedEntityId} RETURN count(m)")
  Page<Comment> getRootComments(Long relatedEntityId, Pageable pageable);

  @Query(
      "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) "
          + " WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = {relatedEntityId} RETURN count(m)")
  long countRootComments(Long relatedEntityId);

  @Query(value =
      "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) "
          + "WHERE id(n1) = {parentId} RETURN m ORDER BY m.created DESC",
      countQuery =
          "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) "
              + "WHERE id(n1) = {parentId} RETURN count(DISTINCT m)")
  Page<Comment> getCommentsFilterByParent(Long parentId, Pageable pageable);

  @Query(value =
      "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) "
          + "WHERE id(n1) = {parentId} RETURN count(DISTINCT m)")
  long countCommentsFilterByParent(Long parentId);
}
