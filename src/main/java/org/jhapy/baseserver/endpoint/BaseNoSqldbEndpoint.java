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

import org.jhapy.baseserver.converter.BaseConverterV2;
import org.jhapy.baseserver.domain.nosqldb.BaseEntity;
import org.jhapy.baseserver.service.CrudNosqldbService;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.BaseEntityStrId;
import org.jhapy.dto.serviceQuery.BaseRemoteQuery;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.generic.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseNoSqldbEndpoint<T extends BaseEntity, D extends BaseEntityStrId>
    implements HasLogger {

  protected final BaseConverterV2 converter;

  protected BaseNoSqldbEndpoint(BaseConverterV2 converter) {
    this.converter = converter;
  }

  protected abstract CrudNosqldbService<T> getService();

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

  protected org.jhapy.dto.utils.Page toDtoPage(
      org.springframework.data.domain.Page domain, List data) {
    org.jhapy.dto.utils.Page result = new org.jhapy.dto.utils.Page<>();
    result.setTotalPages(domain.getTotalPages());
    result.setSize(domain.getSize());
    result.setNumber(domain.getNumber());
    result.setNumberOfElements(domain.getNumberOfElements());
    result.setTotalElements(domain.getTotalElements());
    result.setPageable(converter.convert(domain.getPageable()));
    result.setContent(data);
    return result;
  }

  protected abstract D convertToDto(T domain);

  protected abstract List<D> convertToDtos(Iterable<T> domains);

  protected abstract T convertToDomain(D dto);

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("findAnyMatching");

    Page<T> result =
        getService()
            .findAnyMatching(
                query.getFilter(), query.getShowInactive(), converter.convert(query.getPageable()));
    return handleResult(loggerPrefix, toDtoPage(result, convertToDtos(result.getContent())));
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    var loggerPrefix = getLoggerPrefix("countAnyMatching");

    return handleResult(
        loggerPrefix, getService().countAnyMatching(query.getFilter(), query.getShowInactive()));
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("getById");

    logger().debug(loggerPrefix + "ID =  " + query.getId());

    return handleResult(loggerPrefix, convertToDto(getService().load(query.getId())));
  }

  @PostMapping(value = "/getAll")
  public ResponseEntity<ServiceResult> getAll(@RequestBody BaseRemoteQuery query) {
    var loggerPrefix = getLoggerPrefix("getAll");

    return handleResult(loggerPrefix, convertToDtos(getService().findAll()));
  }

  @PostMapping(value = "/save")
  public ResponseEntity<ServiceResult> save(@RequestBody SaveQuery<D> query) {
    var loggerPrefix = getLoggerPrefix("save");

    return handleResult(
        loggerPrefix, convertToDto(getService().save(convertToDomain(query.getEntity()))));
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    var loggerPrefix = getLoggerPrefix("delete");

    getService().delete(query.getId());
    return handleResult(loggerPrefix);
  }
}