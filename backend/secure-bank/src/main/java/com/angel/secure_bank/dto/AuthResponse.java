package com.angel.secure_bank.dto;

public record AuthResponse(

        String accessToken,
        String tokenType,
        long expiresIn,
        String role

) {
    // Constructor compacto con valor por defecto para tokenType
    public AuthResponse(String accessToken, long expiresIn, String role) {
        this(accessToken, "Bearer", expiresIn, role);
    }
}

// Lo que el backend devuelve después de un login exitoso.
// El frontend guardará este accessToken y lo mandará en cada petición
// dentro del header: Authorization: Bearer <token>
// expiresIn indica en segundos cuánto tiempo es válido el token.