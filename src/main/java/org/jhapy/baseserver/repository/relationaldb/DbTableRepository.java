package org.jhapy.baseserver.repository.relationaldb;

import org.jhapy.baseserver.domain.relationaldb.DbTable;
import org.springframework.stereotype.Repository;

@Repository
public interface DbTableRepository extends BaseRepository<DbTable> {
  DbTable getByTableName(String tableName);

  DbTable getByEntityName(String entityName);
}