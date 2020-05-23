package org.jhapy.baseserver.repository.graphdb;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.jhapy.baseserver.domain.graphdb.BaseEntity;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 3/8/20
 */
public interface BaseRepository<T extends BaseEntity> extends Neo4jRepository<T, Long>{

}