package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.dto.domain.ClientDTO;
import org.jhapy.dto.messageQueue.ClientUpdateTypeEnum;

import java.util.List;

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

  Client getByExternalId(Long externalId);

  List<Client> getByExternalIds(List<Long> externalIds);

  void beforeEntitySave(BaseEntity entity);

  void beforeEntityDelete(BaseEntity entity);

  void beforeEntityLoad(BaseEntity entity);

  <T extends BaseEntity> List<Long> getClientCriteria(Class<T> entityClass);
}