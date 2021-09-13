package org.jhapy.baseserver.service;

import org.apache.commons.text.CaseUtils;
import org.apache.commons.text.WordUtils;
import org.jhapy.baseserver.domain.relationaldb.AccessLevelEnum;
import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.baseserver.domain.relationaldb.DbTable;
import org.jhapy.baseserver.repository.relationaldb.BaseRepository;
import org.jhapy.baseserver.repository.relationaldb.DbTableRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 15/07/2021
 */
@Service
@Transactional(readOnly = true)
public class DbTableServiceImpl implements DbTableService {

  private final DbTableRepository dbTableRepository;
  private final EntityManager entityManager;
  private final DataSource dataSource;
  private final ClientService clientService;

  private boolean hasBootstrapped = false;

  public DbTableServiceImpl(
      EntityManager entityManager,
      DbTableRepository dbTableRepository,
      DataSource dataSource,
      ClientService clientService) {
    this.entityManager = entityManager;
    this.dbTableRepository = dbTableRepository;
    this.dataSource = dataSource;
    this.clientService = clientService;
  }

  @Override
  public DbTable getByEntity(String entity) {
    return dbTableRepository.getByEntityName(entity);
  }

  @Override
  public ClientService getClientService() {
    return clientService;
  }

  @Transactional
  @EventListener(ApplicationReadyEvent.class)
  public void postLoad() {
    bootstrapDbTables();
  }

  @Transactional
  public synchronized void bootstrapDbTables() {

    if (hasBootstrapped) {
      return;
    }

    var loggerPrefix = getLoggerPrefix("bootstrapDbTables");

    try {
      Client systemClient = clientService.getByExternalId(0L);
      if (systemClient == null) {
        error(loggerPrefix, "Missing System client...");
        return;
      }
      DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
      ResultSet tables = metaData.getTables(null, null, null, new String[] {"TABLE"});
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        String entityName = CaseUtils.toCamelCase(tableName, false, '_');
        String name = WordUtils.capitalizeFully(tableName.replaceAll("_", " "));
        debug(
            loggerPrefix,
            "Getting table {0} for entity name {1} and entry name {2}",
            tableName,
            entityName,
            name);
        DbTable dbTable = dbTableRepository.getByTableName(tableName);
        if (dbTable == null) {
          dbTable = new DbTable();
          dbTable.setName(name);
          dbTable.setTableName(tableName);
          dbTable.setEntityName(entityName);
          dbTable.setExternalClientId(systemClient.getExternalClientId());
          dbTable.setAccessLevel(AccessLevelEnum.SYSTEM_PLUS_CLIENT);
          dbTable.setIsChangeLog(false);
          dbTable.setIsDeletable(true);
          dbTable.setIsSecurityEnabled(false);

          dbTableRepository.save(dbTable);
        }
      }
    } catch (SQLException sqlException) {
      error(loggerPrefix, sqlException, "SQL Exception while bootstrap");
    }
  }

  @Override
  public BaseRepository<DbTable> getRepository() {
    return dbTableRepository;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public Class<DbTable> getEntityClass() {
    return DbTable.class;
  }
}