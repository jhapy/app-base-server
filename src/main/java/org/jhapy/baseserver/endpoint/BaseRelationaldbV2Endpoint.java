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

import org.jhapy.baseserver.converter.EntityCommentConverter;
import org.jhapy.baseserver.converter.GenericMapper;
import org.jhapy.baseserver.domain.relationaldb.BaseEntity;
import org.jhapy.baseserver.service.CrudRelationalService;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.BaseEntityLongId;
import org.jhapy.dto.serviceQuery.BaseRemoteQuery;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public abstract class BaseRelationaldbV2Endpoint<T extends BaseEntity, D extends BaseEntityLongId>
    implements HasLogger {

  protected final GenericMapper<T, D> mapper;

  @Autowired protected EntityCommentConverter entityCommentConverter;

  protected BaseRelationaldbV2Endpoint(GenericMapper<T, D> mapper) {
    this.mapper = mapper;
  }

  protected abstract CrudRelationalService<T> getService();

  protected Map<String, Object> getContext(BaseRemoteQuery query) {
    Map<String, Object> context = new HashMap<>();

    context.put("username", query.getQueryUsername());
    context.put("userId", query.getQueryUserId());
    context.put("sessionId", query.getQuerySessionId());
    context.put("iso3Language", query.getQueryIso3Language());
    context.put("currentPosition", query.getQueryCurrentPosition());
    context.put("clientId", query.getQueryExternalClientID());
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

  protected org.jhapy.dto.utils.Page<D> toDtoPage(Page<T> domain, List<D> data) {
    org.jhapy.dto.utils.Page<D> result = new org.jhapy.dto.utils.Page<>();
    result.setTotalPages(domain.getTotalPages());
    result.setSize(domain.getSize());
    result.setNumber(domain.getNumber());
    result.setNumberOfElements(domain.getNumberOfElements());
    result.setTotalElements(domain.getTotalElements());
    result.setPageable(mapper.convert(domain.getPageable()));
    result.setContent(data);
    return result;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");

    defaultFindSecurity();
    readSecurity();

    Page<T> result =
        getService()
            .findAnyMatching(
                query.getFilter(), query.getShowInactive(), mapper.convert(query.getPageable()));
    return handleResult(
        loggerPrefix, toDtoPage(result, mapper.asDTOList(result.getContent(), getContext(query))));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");

    defaultFindSecurity();
    readSecurity();

    return handleResult(
        loggerPrefix, getService().countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById", query.getId());

    return handleResult(
        loggerPrefix, mapper.asDTO(getService().load(query.getId()), getContext(query)));
  }

  @PostMapping(value = "/getAll")
  public ResponseEntity<ServiceResult> getAll(@RequestBody BaseRemoteQuery query) {
    var loggerPrefix = getLoggerPrefix("getAll");

    readSecurity();

    return handleResult(loggerPrefix, mapper.asDTOList(getService().getAll(), getContext(query)));
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(@RequestBody SaveQuery<D> query) {
    saveSecurity();

    return handleSave(query);
  }

  @PostMapping(value = "/saveAll")
  public ResponseEntity<ServiceResult> saveAll(@RequestBody SaveAllQuery<D> query) {
    var loggerPrefix = getLoggerPrefix("saveAll");

    saveSecurity();

    if (query.getParentEntityId() == null) {
      return handleResult(
          loggerPrefix,
          mapper.asDTOList(
              getService().saveAll(mapper.asEntityList(query.getEntity(), getContext(query))),
              getContext(query)));
    } else {
      return handleResult(
          loggerPrefix,
          mapper.asDTOList(
              getService()
                  .saveAll(
                      query.getParentEntityId(),
                      mapper.asEntityList(query.getEntity(), getContext(query))),
              getContext(query)));
    }
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete", query.getId());

    deleteSecurity();

    getService().delete(query.getId());
    return handleResult(loggerPrefix);
  }

  public void deleteSecurity() {}

  public void saveSecurity() {}

  public void readSecurity() {}

  public void defaultFindSecurity() {}
  ;

  public ResponseEntity<ServiceResult> handleSave(SaveQuery<D> query) {
    var loggerPrefix = getLoggerPrefix("save");

    return handleResult(
        loggerPrefix,
        mapper.asDTO(
            getService().save(mapper.asEntity(query.getEntity(), getContext(query))),
            getContext(query)));
  }
}