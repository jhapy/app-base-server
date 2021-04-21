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

package org.jhapy.baseserver.endpoint;

import ma.glasnost.orika.MappingContext;
import org.jhapy.baseserver.domain.nosqldb.BaseEntity;
import org.jhapy.baseserver.service.CrudNosqldbService;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.commons.utils.OrikaBeanMapper;
import org.jhapy.dto.domain.BaseEntityStrId;
import org.jhapy.dto.serviceQuery.BaseRemoteQuery;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.CountAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.FindAnyMatchingQuery;
import org.jhapy.dto.serviceQuery.generic.GetByStrIdQuery;
import org.jhapy.dto.serviceQuery.generic.SaveQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseNoSqldbEndpoint<T extends BaseEntity, D extends BaseEntityStrId> implements
    HasLogger {

  protected final OrikaBeanMapper mapperFacade;

  protected BaseNoSqldbEndpoint(OrikaBeanMapper mapperFacade) {
    this.mapperFacade = mapperFacade;
  }

  protected abstract CrudNosqldbService<T> getService();

  protected abstract Class<T> getEntityClass();

  protected abstract Class<D> getDtoClass();

  protected MappingContext getOrikaContext(BaseRemoteQuery query) {
    MappingContext context = new MappingContext.Factory().getContext();

    context.setProperty("username", query.getQueryUsername());
    context.setProperty("userId", query.getQueryUserId());
    context.setProperty("sessionId", query.getQuerySessionId());
    context.setProperty("iso3Language", query.getQueryIso3Language());
    context.setProperty("currentPosition", query.getQueryCurrentPosition());

    return context;
  }

  protected ResponseEntity<ServiceResult> handleResult(String loggerPrefix) {
    return handleResult(loggerPrefix, new ServiceResult<>());
  }

  protected ResponseEntity<ServiceResult> handleResult(String loggerPrefix, Object result) {
    return handleResult(loggerPrefix, new ServiceResult<>(result));
  }

  protected ResponseEntity<ServiceResult> handleResult(String loggerPrefix, String error) {
    return handleResult(loggerPrefix, new ServiceResult<>(error));
  }

  protected ResponseEntity<ServiceResult> handleResult(String loggerPrefix, ServiceResult result) {
    if (result.getIsSuccess()) {
      ResponseEntity<ServiceResult> response = ResponseEntity.ok(result);
      if (logger().isTraceEnabled()) {
        logger().debug(loggerPrefix + "Response OK : " + result);
      }
      return response;
    } else {
      logger().error(loggerPrefix + "Response KO : " + result.getMessage());
      return ResponseEntity.ok(result);
    }
  }

  protected ResponseEntity<ServiceResult> handleResult(String loggerPrefix, Throwable throwable) {
    logger()
        .error(loggerPrefix + "Response KO with Exception : " + throwable.getMessage(), throwable);
    return ResponseEntity.ok(new ServiceResult<>(throwable));
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(
      @RequestBody FindAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("findAnyMatching");
    try {
      Page<T> result = getService()
          .findAnyMatching(query.getFilter(), query.getShowInactive(),
              mapperFacade.map(query.getPageable(),
                  Pageable.class));
      return handleResult(loggerPrefix, mapperFacade
          .map(result, org.jhapy.dto.utils.Page.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(
      @RequestBody CountAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("countAnyMatching");
    try {
      return handleResult(loggerPrefix,
          getService().countAnyMatching(query.getFilter(), query.getShowInactive()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("getById");
    try {
      logger().debug(loggerPrefix + "ID =  " + query.getId());

      return handleResult(loggerPrefix, mapperFacade
          .map(getService().load(query.getId()),
              getDtoClass(),
              getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getAll")
  public ResponseEntity<ServiceResult> getAll(@RequestBody BaseRemoteQuery query) {
    String loggerPrefix = getLoggerPrefix("getAll");
    try {
      return handleResult(loggerPrefix, mapperFacade
          .mapAsList(getService().findAll(),
              getDtoClass(),
              getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(
      @RequestBody SaveQuery<D> query) {
    String loggerPrefix = getLoggerPrefix("save");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(getService()
              .save(mapperFacade
                  .map(query.getEntity(), getEntityClass(), getOrikaContext(query))),
          getDtoClass(),
          getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("delete");
    try {
      getService().delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}
