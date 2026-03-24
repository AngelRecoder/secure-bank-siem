package com.angel.secure_bank.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}

// Estructura estándar para todos los errores de la API.
// Siempre devolvemos el mismo formato sin importar qué falló,
// esto evita filtrar stack traces o mensajes internos al cliente.