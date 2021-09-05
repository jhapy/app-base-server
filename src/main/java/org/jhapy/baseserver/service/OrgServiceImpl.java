package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.Org;
import org.jhapy.baseserver.repository.relationaldb.BaseRepository;
import org.jhapy.baseserver.repository.relationaldb.OrgRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 15/07/2021
 */
@Service
@Transactional(readOnly = true)
public class OrgServiceImpl implements OrgService {

  private final OrgRepository orgRepository;
  private final EntityManager entityManager;
  private final ClientService clientService;

  public OrgServiceImpl(
      EntityManager entityManager, OrgRepository orgRepository, ClientService clientService) {
    this.entityManager = entityManager;
    this.orgRepository = orgRepository;
    this.clientService = clientService;
  }

  @Override
  public ClientService getClientService() {
    return clientService;
  }

  @Override
  public BaseRepository<Org> getRepository() {
    return orgRepository;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public Class<Org> getEntityClass() {
    return Org.class;
  }
}