package com.angel.secure_bank.service;

import com.angel.secure_bank.dto.AuditLogResponse;
import com.angel.secure_bank.model.AuditLog;
import com.angel.secure_bank.model.AuditSeverity;
import com.angel.secure_bank.model.User;
import com.angel.secure_bank.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String eventType, String description, User user,
                    String ipAddress, String userAgent, AuditSeverity severity) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .description(description)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .severity(severity)
                .build();

        auditLogRepository.save(log);
    }

    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuditLogResponse> getLogsBySeverity(AuditSeverity severity) {
        return auditLogRepository.findBySeverityOrderByCreatedAtDesc(severity)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getEventType(),
                log.getDescription(),
                log.getUser() != null ? log.getUser().getEmail() : "system",
                log.getIpAddress(),
                log.getSeverity().name(),
                log.getCreatedAt()
        );
    }
}

// Servicio central del SIEM.
// El método log() lo llamaremos desde otros servicios cada vez que
// ocurra algo relevante: login exitoso, login fallido, transferencia, etc.
// getLogsBySeverity() permite al admin filtrar solo los eventos críticos
// sin tener que revisar todo el historial.