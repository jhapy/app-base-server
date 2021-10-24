package org.jhapy.baseserver.service;

import org.apache.commons.lang3.StringUtils;
import org.jhapy.baseserver.client.mailcow.DomainClient;
import org.jhapy.baseserver.converter.ClientConverter;
import org.jhapy.baseserver.domain.relationaldb.AccessLevelEnum;
import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.jhapy.baseserver.domain.relationaldb.Client;
import org.jhapy.baseserver.domain.relationaldb.DbTable;
import org.jhapy.baseserver.repository.relationaldb.BaseRepository;
import org.jhapy.baseserver.repository.relationaldb.ClientRepository;
import org.jhapy.baseserver.utils.SessionData;
import org.jhapy.commons.config.AppProperties;
import org.jhapy.commons.utils.SpringApplicationContext;
import org.jhapy.dto.domain.ClientDTO;
import org.jhapy.dto.messageQueue.ClientUpdate;
import org.jhapy.dto.messageQueue.ClientUpdateTypeEnum;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 15/07/2021
 */
@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {
  private final AmqpTemplate amqpTemplate;
  private final FanoutExchange clientUpdateFanout;
  private final ClientRepository clientRepository;
  private final EntityManager entityManager;
  private final ClientConverter clientConverter;
  private final DbTableService dbTableService;
  private final DomainClient domainClient;
  private final AppProperties appProperties;
  private ThreadLocal<Boolean> propagateChanges = new ThreadLocal<>();

  public ClientServiceImpl(
      AmqpTemplate amqpTemplate,
      @Qualifier("clientUpdate") FanoutExchange clientUpdateFanout,
      EntityManager entityManager,
      ClientRepository clientRepository,
      ClientConverter clientConverter,
      @Lazy DbTableService dbTableService,
      DomainClient domainClient,
      AppProperties appProperties) {
    this.entityManager = entityManager;
    this.clientRepository = clientRepository;
    this.amqpTemplate = amqpTemplate;
    this.clientUpdateFanout = clientUpdateFanout;
    this.clientConverter = clientConverter;
    this.dbTableService = dbTableService;
    this.domainClient = domainClient;
    this.appProperties = appProperties;
  }

  @Override
  public void beforeEntitySave(BaseEntity entity) {
    if (!appProperties.getSecurity().getUseClientSecurity()) return;
    SessionData sessionDataFromContext = SpringApplicationContext.getBean(SessionData.class);
    DbTable dbTable = dbTableService.getByEntity(entity.getClass().getSimpleName());
    /*
            if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_ONLY && sessionDataFromContext.getExternalClientID() != 0) {
                throw new SecurityException("System only entity can only be saved by the system or a system administrator");
            }
            if (dbTable.getAccessLevel() == AccessLevelEnum.CLIENT_ONLY && (sessionDataFromContext.getExternalClientID() == 0 ||
                    (entity.getExternalClientId() != null && !entity.getExternalClientId().equals(sessionDataFromContext.getExternalClientID())))) {
                throw new SecurityException("Client only entity can only be saved by a user WITH the same client");
            }
    */
    if (dbTable.getAccessLevel() == AccessLevelEnum.CLIENT_ONLY
        || dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_PLUS_CLIENT) {
      entity.setExternalClientId(sessionDataFromContext.getExternalClientID());
    } else if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_ONLY) {
      entity.setExternalClientId(null);
    }
  }

  @Override
  public void beforeEntityDelete(BaseEntity entity) {
    if (!appProperties.getSecurity().getUseClientSecurity()) return;
    SessionData sessionDataFromContext = SpringApplicationContext.getBean(SessionData.class);
    DbTable dbTable = dbTableService.getByEntity(entity.getClass().getSimpleName());
    if (!dbTable.getIsDeletable()) {
      throw new SecurityException("Cannot delete a non deletable record");
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_ONLY
        && sessionDataFromContext.getExternalClientID() != null) {
      throw new SecurityException(
          "System only entity can only be deleted by the system or a system administrator");
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.CLIENT_ONLY
        && (sessionDataFromContext.getExternalClientID() == null
            || (entity.getExternalClientId() != null
                && !entity
                    .getExternalClientId()
                    .equals(sessionDataFromContext.getExternalClientID())))) {
      throw new SecurityException(
          "Client only entity can only be deleted by a user WITH the same client");
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_PLUS_CLIENT
        && entity.getExternalClientId() != null
        && !entity.getExternalClientId().equals(sessionDataFromContext.getExternalClientID()))
      throw new SecurityException(
          "System+Client entity can only be accessed by a user WITH the same client");
  }

  @Override
  public void beforeEntityLoad(BaseEntity entity) {
    if (!appProperties.getSecurity().getUseClientSecurity()) return;

    SessionData sessionDataFromContext = SpringApplicationContext.getBean(SessionData.class);
    DbTable dbTable = dbTableService.getByEntity(entity.getClass().getSimpleName());
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_ONLY
        && sessionDataFromContext.getExternalClientID() != null) {
      throw new SecurityException(
          "System only entity can only be accessed by the system or a system administrator");
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.CLIENT_ONLY
        && (sessionDataFromContext.getExternalClientID() == null
            || (entity.getExternalClientId() != null
                && !entity
                    .getExternalClientId()
                    .equals(sessionDataFromContext.getExternalClientID())))) {
      throw new SecurityException(
          "Client only entity can only be accessed by a user WITH the same client");
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_PLUS_CLIENT
        && (entity.getExternalClientId() != null
                && sessionDataFromContext.getExternalClientID() != null
            || (sessionDataFromContext.getExternalClientID() != null
                && entity.getExternalClientId() != null
                && !entity
                    .getExternalClientId()
                    .equals(sessionDataFromContext.getExternalClientID()))))
      throw new SecurityException(
          "System+Client entity can only be accessed by a user WITH the same client");
  }

  @Override
  public <T extends BaseEntity> List<UUID> getClientCriteria(Class<T> entityClass) {
    if (!appProperties.getSecurity().getUseClientSecurity()) return Collections.emptyList();
    SessionData sessionDataFromContext = SpringApplicationContext.getBean(SessionData.class);
    DbTable dbTable = dbTableService.getByEntity(entityClass.getSimpleName());
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_ONLY) {
      return Collections.emptyList();
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.CLIENT_ONLY) {
      return Collections.singletonList(sessionDataFromContext.getExternalClientID());
    }
    if (dbTable.getAccessLevel() == AccessLevelEnum.SYSTEM_PLUS_CLIENT) {
      return Collections.emptyList(); // Arrays.asList( 0L, sessionDataFromContext.getClientID() );
    }

    return Collections.emptyList();
  }

  @Override
  public ClientService getClientService() {
    return this;
  }

  @Override
  public Client save(Client entity) {
    if (entity.getExternalId() == null) {
      entity.setExternalVersion(0L);
      entity.setExternalId(UUID.randomUUID());
    } else {
      if (entity.getId() != null) {
        entity.setExternalVersion(entity.getExternalVersion() + 1);
      }
    }
    propagateChanges.set(true);
    var savedClient = ClientService.super.save(entity);

    if (appProperties.getMailcow().getEnabled() && savedClient.getIsMailboxDomainCreated() == null
        || (!savedClient.getIsMailboxDomainCreated()
            && StringUtils.isNotBlank(savedClient.getMailboxDomain()))) {
      domainClient.addDomain(
          entity.getMailboxDomain(),
          "",
          appProperties.getMailcow().getDefaultMaxMailboxesForDomain(),
          appProperties.getMailcow().getDefaultMaxQuotaPerMailbox(),
          appProperties.getMailcow().getDefaultMaxQuotaForDomain(),
          appProperties.getMailcow().getDefaultMailboxQuotaForDomain(),
          appProperties.getMailcow().getDefaultCountOfAliasesForDomain());
      savedClient.setIsMailboxDomainCreated(true);

      savedClient = ClientService.super.save(entity);
    }
    return savedClient;
  }

  @Override
  public void postPersist(Client client) {
    if (propagateChanges.get() != null && propagateChanges.get()) {
      ClientUpdate clientUpdate = new ClientUpdate();
      clientUpdate.setClientDTO(clientConverter.asDTO(client, null));
      clientUpdate.setUpdateType(ClientUpdateTypeEnum.INSERT);
      amqpTemplate.convertAndSend(clientUpdateFanout.getName(), "", clientUpdate);
    }
  }

  @Override
  public void postUpdate(Client client) {
    if (propagateChanges.get() != null && propagateChanges.get()) {
      ClientUpdate clientUpdate = new ClientUpdate();
      clientUpdate.setClientDTO(clientConverter.asDTO(client, null));
      clientUpdate.setUpdateType(ClientUpdateTypeEnum.UPDATE);
      amqpTemplate.convertAndSend(clientUpdateFanout.getName(), "", clientUpdate);
    }
  }

  @Override
  public void postRemove(Client client) {
    ClientUpdate clientUpdate = new ClientUpdate();
    clientUpdate.setClientDTO(clientConverter.asDTO(client, null));
    clientUpdate.setUpdateType(ClientUpdateTypeEnum.DELETE);
    amqpTemplate.convertAndSend(clientUpdateFanout.getName(), "", clientUpdate);
  }

  @Override
  @Transactional
  public void clientUpdate(ClientUpdateTypeEnum updateType, ClientDTO client) {
    var existingClient = clientRepository.getByExternalId(client.getExternalId());
    if (existingClient.isPresent() && updateType.equals(ClientUpdateTypeEnum.DELETE)) {
      clientRepository.deleteById(existingClient.get().getId());
    }

    if (existingClient.isEmpty()
        || !existingClient.get().getExternalVersion().equals(client.getExternalVersion())) {
      existingClient.ifPresent(value -> client.setId(value.getId()));
      Client newClient = clientConverter.asEntity(client, null);
      propagateChanges.set(false);
      clientRepository.save(newClient);
    }
  }

  @Override
  @Cacheable("clients")
  public Client getByExternalId(UUID externalId) {
    return clientRepository.getByExternalId(externalId).orElse(null);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<Client> getByExternalIds(List<UUID> externalIds) {
    return clientRepository.getByExternalIdIn(externalIds);
  }

  @Override
  public BaseRepository<Client> getRepository() {
    return clientRepository;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public Class<Client> getEntityClass() {
    return Client.class;
  }
}