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
import java.util.concurrent.atomic.AtomicLong;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.audit.AuditLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
@ConditionalOnProperty(prefix = "javers", name = "enabled")
@Service
public class AuditLogServiceImpl implements AuditLogService, HasLogger {

  private final Javers javers;

  public AuditLogServiceImpl(Javers javers) {
    this.javers = javers;
  }

  @Override
  public Page<AuditLog> getAudit(String className, String id, Pageable pageable) {
    var loggerPrefix = getLoggerPrefix("getAudit", className, id, pageable);

    Changes changes = javers.findChanges(QueryBuilder.byInstanceId(id, className)
        .withNewObjectChanges().skip((int) pageable.getOffset())
        .build());

    logger().debug(loggerPrefix + "Found changes = " + changes.size());

    AtomicLong index = new AtomicLong(0);
    List<AuditLog> auditLogs = new ArrayList<>();
    changes.groupByCommit().forEach(byCommit -> byCommit.groupByObject().forEach(byObject -> byObject.get().forEach(change -> {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(index.incrementAndGet());
        auditLog.setCommit(byCommit.getCommit().getId().value());
        auditLog.setAuthor(byCommit.getCommit().getAuthor());
        auditLog.setDate(byCommit.getCommit().getCommitDate());
        auditLog.setChange(change.toString());
        auditLogs.add(auditLog);
      })));

    logger().debug(loggerPrefix + "Audit Log size = " + auditLogs.size());

    return new PageImpl(auditLogs, pageable, changes.size());
  }

  @Override
  public long countAudit(String className, String id) {
    var loggerPrefix = getLoggerPrefix("countAudit", className, id);

    long count = javers.findChanges(QueryBuilder.byInstanceId(id, className)
        .withNewObjectChanges().build()).size();
    logger().debug(loggerPrefix + "Count = " + count);

    return count;
  }
}
