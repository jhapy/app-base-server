package org.jhapy.baseserver.repository.nosqldb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.jhapy.baseserver.domain.nosqldb.BaseEntity;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 3/8/20
 */
public interface BaseRepository<T extends BaseEntity> extends MongoRepository<T, String> {

}