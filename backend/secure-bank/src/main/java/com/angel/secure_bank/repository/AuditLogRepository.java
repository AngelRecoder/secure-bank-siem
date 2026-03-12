package com.angel.secure_bank.repository;

import com.angel.secure_bank.model.AuditLog;
import com.angel.secure_bank.model.AuditSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findBySeverityOrderByCreatedAtDesc(AuditSeverity severity);

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from,
            LocalDateTime to
    );
}

// Repositorio central del SIEM.
// findBySeverity nos permite filtrar eventos críticos en el dashboard.
// findByCreatedAtBetween nos da eventos en un rango de tiempo,
// útil para investigar incidentes: "qué pasó entre las 2am y las 3am".