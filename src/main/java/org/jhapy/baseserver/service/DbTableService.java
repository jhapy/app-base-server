package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.DbTable;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 15/07/2021
 */
public interface DbTableService extends CrudRelationalService<DbTable> {
  DbTable getByEntity(String entity);
}