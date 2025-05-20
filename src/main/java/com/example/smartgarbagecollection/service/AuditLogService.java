package com.example.smartgarbagecollection.service;

public interface AuditLogService {
    void log(String action, String details);
    void logSystem(String action, String details);
}