package com.angel.secure_bank.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String eventType,
        String description,
        String userEmail,
        String ipAddress,
        String severity,
        LocalDateTime createdAt
) {}

// Lo que el admin ve en el dashboard de auditoría.
// Incluimos el email del usuario involucrado, la IP desde donde
// se hizo la acción y la severidad para poder filtrar eventos críticos.