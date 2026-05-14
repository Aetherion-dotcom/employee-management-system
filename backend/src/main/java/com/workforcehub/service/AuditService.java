package com.workforcehub.service;

import com.workforcehub.entity.AuditLog;
import com.workforcehub.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for audit logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an audit entry asynchronously.
     */
    @Async
    public void log(String username, String action, String entityType, Long entityId, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .details(details)
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AuditLog> getAuditLogsByUser(String username, Pageable pageable) {
        return auditLogRepository.findByUsernameOrderByCreatedAtDesc(username, pageable);
    }
}
