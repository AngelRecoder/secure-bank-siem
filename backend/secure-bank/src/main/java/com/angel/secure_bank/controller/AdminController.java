package com.angel.secure_bank.controller;

import com.angel.secure_bank.dto.AuditLogResponse;
import com.angel.secure_bank.model.AuditSeverity;
import com.angel.secure_bank.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuditService auditService;

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }

    @GetMapping("/logs/severity/{severity}")
    public ResponseEntity<List<AuditLogResponse>> getLogs(@PathVariable AuditSeverity severity) {
        return ResponseEntity.ok(auditService.getLogsBySeverity(severity));
    }
}

// @PreAuthorize("hasRole('ADMIN')") a nivel de clase aplica la restricción
// a todos los métodos del controlador, doble protección junto con la regla
// que ya tenemos en SecurityConfig.
// El endpoint de severidad recibe CRITICAL, WARNING o INFO como parámetro
// en la URL, por ejemplo: /api/admin/logs/severity/CRITICAL