package org.jhapy.baseserver.service;

import org.jhapy.dto.domain.audit.AuditLog;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
@Service
public class AuditLogServiceImpl implements AuditLogService {
private final Javers javers;

  public AuditLogServiceImpl(Javers javers) {
    this.javers = javers;
  }

  @Override
  public Page<AuditLog> getAudit(String className, Long id, Pageable pageable) {
    Changes changes = javers.findChanges(QueryBuilder.byInstanceId(id,className )
        .withNewObjectChanges().skip((int) pageable.getOffset()).limit(pageable.getPageSize()).build());

    AtomicLong index = new AtomicLong(0);
    List<AuditLog> auditLogs = new ArrayList<>();
    changes.groupByCommit().forEach(byCommit -> {
      byCommit.groupByObject().forEach(byObject -> {
        byObject.get().forEach(change -> {
          AuditLog auditLog = new AuditLog();
          auditLog.setId(index.incrementAndGet());
          auditLog.setCommit( byCommit.getCommit().getId().toString() );
          auditLog.setAuthor( byCommit.getCommit().getAuthor());
          auditLog.setDate( byCommit.getCommit().getCommitDate());
          auditLog.setChange( change.toString());
          auditLogs.add(auditLog);
        });
      });
    });
return new PageImpl(auditLogs, pageable, changes.size());
  }

  @Override
  public long countAudit(String className, Long id) {
    return javers.findChanges(QueryBuilder.byInstanceId(id,className )
        .withNewObjectChanges().build()).size();
  }
}
