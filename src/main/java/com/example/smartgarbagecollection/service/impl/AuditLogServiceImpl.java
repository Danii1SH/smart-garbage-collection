package com.example.smartgarbagecollection.service.impl;

import com.example.smartgarbagecollection.model.AuditLog;
import com.example.smartgarbagecollection.repository.AuditLogRepository;
import com.example.smartgarbagecollection.security.UserDetailsImpl;
import com.example.smartgarbagecollection.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(String action, String details) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl user) {
            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setDetails(details);
            log.setTimestamp(LocalDateTime.now());
            log.setPerformedByUserId(user.getUserId());
            log.setPerformedByCompanyId(user.getCompanyId());
            log.setPerformedByRole(user.getRole());

            auditLogRepository.save(log);
        } else {
            logSystem(action, details);
        }
    }

    @Override
    public void logSystem(String action, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}
