/*
 * Copyright 2020-2020 the original author or authors from the JHapy project.
 *
 * This file is part of the JHapy project, see https://www.jhapy.org/ for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jhapy.baseserver.service;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.baseserver.domain.nosqldb.BaseEntity;
import org.jhapy.commons.exception.ServiceException;
import org.jhapy.commons.security.SecurityUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-26
 */
public interface CrudNosqldbService<T extends BaseEntity> extends HasLogger {

  MongoRepository<T, String> getRepository();

  MongoTemplate getMongoTemplate();

  Class<T> getEntityClass();

  @Transactional
  default T save(T entity) throws ServiceException {
    return getRepository().save(entity);
  }

  @Transactional
  default void delete(T entity) {
    if (entity == null) {
      throw new EntityNotFoundException();
    }
    getRepository().delete(entity);
  }

  @Transactional
  default void delete(String id) {
    delete(load(id));
  }

  default long count() {
    return getRepository().count();
  }

  default T load(String id) {
    T entity = getRepository().findById(id).orElse(null);
    if (entity == null) {
      throw new EntityNotFoundException();
    }
    return entity;
  }

  default Iterable<T> findAll() {
    return getRepository().findAll();
  }

  default Page<T> findAnyMatching(String filter, Boolean showInactive, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");

    logger().debug(
        loggerString + "----------------------------------");

    String currentUser = SecurityUtils.getCurrentUserLogin().orElse(null);

    logger().debug(
        loggerString + "In, Filter = " + filter + ", User = "
            + currentUser + ", Show Inactive = "
            + showInactive + ", Pageable = " + pageable);

    Criteria criteria = new Criteria();

    buildSearchQuery(criteria, currentUser, filter, showInactive);

    Query query = new Query(criteria);
    if (pageable.isPaged()) {
      query.with(pageable);
    }

    List<T> result = getMongoTemplate().find(query, getEntityClass());

    long nbRecords = getMongoTemplate().count(Query.of(query).limit(-1).skip(-1), getEntityClass());

    Page<T> pagedResult;

    if (pageable.isPaged()) {
      pagedResult = PageableExecutionUtils.getPage(
          result,
          pageable,
          () -> nbRecords);
    } else {
      pagedResult = new PageImpl<>(result, pageable, nbRecords);
    }

    logger()
        .debug(loggerString + "Out : Elements = " + pagedResult.getContent().size() + " of "
            + pagedResult
            .getTotalElements() + ", Page = " + pagedResult.getNumber() + " of " + pagedResult
            .getTotalPages());

    return pagedResult;
  }

  default long countAnyMatching(String filter, Boolean showInactive) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");

    logger().debug(
        loggerPrefix + "----------------------------------");

    String currentUser = SecurityUtils.getCurrentUserLogin().orElse(null);

    logger().debug(
        loggerPrefix + "In, Filter = " + filter + ", User = "
            + currentUser + ", Show Inactive = "
            + showInactive);

    Criteria criteria = new Criteria();

    buildSearchQuery(criteria, currentUser, filter, showInactive);

    Query query = new Query(criteria);

    logger().debug(loggerPrefix + "Query = " + query);

    long nbRecords = getMongoTemplate().count(query, getEntityClass());

    logger().debug(loggerPrefix + "Out = " + nbRecords + " items");

    return nbRecords;
  }

  default void buildSearchQuery(Criteria rootCriteria, String currentUserId, String filter,
      Boolean showInactive) {
    List<Criteria> orPredicates = new ArrayList<>();
    List<Criteria> andPredicated = new ArrayList<>();

    if (StringUtils.isNotBlank(filter)) {
      Pattern pattern = Pattern
          .compile(filter, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
      orPredicates.add(Criteria.where("name").regex(pattern));
      orPredicates.add(Criteria.where("description").regex(pattern));
    }

    if (showInactive == null || !showInactive) {
      andPredicated.add(Criteria.where("isActive").is(Boolean.TRUE));
    }

    if (!orPredicates.isEmpty()) {
      andPredicated.add(new Criteria().orOperator(orPredicates.toArray(new Criteria[0])));
    }

    if (!andPredicated.isEmpty()) {
      rootCriteria.andOperator(andPredicated.toArray(new Criteria[0]));
    }
  }
}
