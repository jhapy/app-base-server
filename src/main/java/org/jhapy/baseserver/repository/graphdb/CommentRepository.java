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

package org.jhapy.baseserver.repository.graphdb;

import org.jhapy.baseserver.domain.graphdb.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-14
 */
public interface CommentRepository extends BaseRepository<Comment> {

  @Query(
      value =
          "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = $relatedEntityId RETURN m ORDER BY m.created DESC SKIP $skip LIMIT $limit",
      countQuery =
          "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = $relatedEntityId RETURN count(m)")
  Page<Comment> getRootComments(@Param("relatedEntityId") UUID relatedEntityId, Pageable pageable);

  @Query(
      "MATCH (m:Comment)-[:HAS_RELATED_ENTITY]->(p) WHERE NOT ((m)-[:HAS_PARENT]->()) AND id(p) = $relatedEntityId RETURN count(m)")
  long countRootComments(@Param("relatedEntityId") UUID relatedEntityId);

  @Query(
      value =
          "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) WHERE id(n1) = $parentId RETURN m ORDER BY m.created DESC SKIP $skip LIMIT $limit",
      countQuery =
          "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) WHERE id(n1) = $parentId RETURN count(DISTINCT m)")
  Page<Comment> getCommentsFilterByParent(@Param("parentId") UUID parentId, Pageable pageable);

  @Query(
      value =
          "MATCH (m:Comment)-[:HAS_PARENT]->(n1:Comment) WHERE id(n1) = $parentId RETURN count(DISTINCT m)")
  long countCommentsFilterByParent(@Param("parentId") UUID parentId);
}