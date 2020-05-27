package org.jhapy.baseserver.repository.relationaldb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 3/8/20
 */
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>,
    QueryByExampleExecutor<T>, JpaSpecificationExecutor<T> {

}