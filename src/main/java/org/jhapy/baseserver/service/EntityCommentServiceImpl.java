package org.jhapy.baseserver.service;

import org.jhapy.baseserver.domain.relationaldb.EntityComment;
import org.jhapy.baseserver.repository.relationaldb.BaseRepository;
import org.jhapy.baseserver.repository.relationaldb.EntityCommentRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 02/08/2020
 */
@Service
public class EntityCommentServiceImpl implements EntityCommentService {
  private final EntityCommentRepository entityCommentRepository;
  private final EntityManager entityManager;
  private final ClientService clientService;

  public EntityCommentServiceImpl(
      EntityCommentRepository entityCommentRepository,
      EntityManager entityManager,
      ClientService clientService) {
    this.entityCommentRepository = entityCommentRepository;
    this.entityManager = entityManager;
    this.clientService = clientService;
  }

  @Override
  public ClientService getClientService() {
    return clientService;
  }

  @Override
  public List<EntityComment> getEntityComments(
      Long relatedEntityId, String relatedEntityName, Instant since) {
    return entityCommentRepository.findByRelatedEntityIdAndRelatedEntityNameAfterSince(
        relatedEntityId, relatedEntityName, since);
  }

  @Override
  public Specification<EntityComment> buildSearchQuery(
      String filter, Boolean showInactive, Object... otherCriteria) {
    return new Specification<EntityComment>() {
      @Override
      public Predicate toPredicate(
          Root<EntityComment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (predicates.isEmpty()) return null;
        else return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
      }
    };
  }

  @Override
  public BaseRepository<EntityComment> getRepository() {
    return entityCommentRepository;
  }

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public Class<EntityComment> getEntityClass() {
    return EntityComment.class;
  }
}