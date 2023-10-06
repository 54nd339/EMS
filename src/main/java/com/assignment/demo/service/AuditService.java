package com.assignment.demo.service;

import com.assignment.demo.dao.AuditRepo;
import com.assignment.demo.model.Audit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@Slf4j
public class AuditService {
    @Autowired
    @Qualifier("Mongo")
    private AuditRepo auditRepo;

    public void createAudit(String action) {
        Audit audit = new Audit();
        audit.setId(UUID.randomUUID().toString());
        audit.setAction(action);
        audit.setTimestamp(new Timestamp(System.currentTimeMillis()));

        auditRepo.createAudit(audit);
        log.info("Created Audit {}", audit);
    }
}
