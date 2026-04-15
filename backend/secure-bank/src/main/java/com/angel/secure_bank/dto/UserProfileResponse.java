package com.angel.secure_bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        BigDecimal balance,
        String role,
        LocalDateTime createdAt
) {}

// Lo que devolvemos cuando alguien pide su perfil.
// No incluimos password ni failedLoginAttempts ni ningún campo sensible,
// solo lo que el usuario necesita ver en su dashboard.