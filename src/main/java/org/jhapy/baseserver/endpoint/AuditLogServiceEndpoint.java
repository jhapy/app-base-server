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

import org.jhapy.baseserver.service.AuditLogService;
import org.jhapy.commons.endpoint.BaseEndpoint;
import org.jhapy.commons.utils.OrikaBeanMapper;
import org.jhapy.dto.domain.audit.AuditLog;
import org.jhapy.dto.serviceQuery.ServiceResult;
import org.jhapy.dto.serviceQuery.auditLog.CountAuditLogQuery;
import org.jhapy.dto.serviceQuery.auditLog.FindAuditLogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/auditLogService")
public class AuditLogServiceEndpoint extends BaseEndpoint {

  private final AuditLogService auditLogService;

  public AuditLogServiceEndpoint(AuditLogService auditLogService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.auditLogService = auditLogService;
  }

  @PostMapping(value = "/getAuditLog")
  public ResponseEntity<ServiceResult> getAuditLog(
      @RequestBody FindAuditLogQuery query) {
    String loggerPrefix = getLoggerPrefix("getAuditLog");
    try {
      Page<AuditLog> result = auditLogService
          .getAudit(query.getClassName(), query.getRecordId(),
              mapperFacade.map(query.getPageable(),
                  Pageable.class));
      return handleResult(loggerPrefix, mapperFacade
          .map(result, org.jhapy.dto.utils.Page.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/countAuditLog")
  public ResponseEntity<ServiceResult> countAuditLog(
      @RequestBody CountAuditLogQuery query) {
    String loggerPrefix = getLoggerPrefix("countAuditLog");
    try {
      return handleResult(loggerPrefix,
          auditLogService
              .countAudit(query.getClassName(), query.getRecordId()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}