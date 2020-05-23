package org.jhapy.baseserver.service;

import org.jhapy.dto.domain.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
public interface AuditLogService {
  Page<AuditLog> getAudit( String className, Long id, Pageable pageable);

  long countAudit( String className, Long id );
}
