package com.workforcehub.audit;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class AuditListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        logAudit(entity, "CREATE");
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        logAudit(entity, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        logAudit(entity, "DELETE");
    }

    private void logAudit(Object entity, String action) {
        // In a real implementation, we would inject a Repository via a BeanFactory to save the log to DB.
        // For now, we log it to console to simulate the Hook.
        String entityName = entity.getClass().getSimpleName();
        log.info("AUDIT LOG -> Action: {} | Entity: {} | Timestamp: {}", action, entityName, LocalDateTime.now());
    }
}
