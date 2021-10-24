package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.dto.domain.ClientDTO;
import org.jhapy.dto.messageQueue.ClientUpdateTypeEnum;

import java.util.List;
import java.util.UUID;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 15/07/2021
 */
public interface ClientService extends CrudRelationalService<Client> {

  void postPersist(Client client);

  void postUpdate(Client client);

  void postRemove(Client client);

  void clientUpdate(ClientUpdateTypeEnum updateType, ClientDTO client);

  Client getByExternalId(UUID externalId);

  List<Client> getByExternalIds(List<UUID> externalIds);

  void beforeEntitySave(BaseEntity entity);

  void beforeEntityDelete(BaseEntity entity);

  void beforeEntityLoad(BaseEntity entity);

  <T extends BaseEntity> List<UUID> getClientCriteria(Class<T> entityClass);
}