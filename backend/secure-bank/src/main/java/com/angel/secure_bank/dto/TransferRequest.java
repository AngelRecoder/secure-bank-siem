package com.angel.secure_bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "Recipient email is required")
        String recipientEmail,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        String description

) {}

// Lo que el frontend manda para hacer una transferencia.
// Usamos el email del destinatario en lugar de su ID
// para evitar que alguien intente adivinar IDs de otros usuarios (IDOR).
// El monto mínimo es 0.01 para evitar transferencias de cero.