package com.angel.secure_bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        Long transactionId,
        String senderEmail,
        String recipientEmail,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {}

// Lo que devolvemos después de una transferencia exitosa.
// Incluimos el ID de transacción para que el usuario pueda
// rastrearlo en su historial.