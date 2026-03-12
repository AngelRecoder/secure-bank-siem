package com.angel.secure_bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password

) {}

// Lo que el frontend manda cuando un usuario se registra.
// Usamos Record porque estos datos no cambian una vez que llegan.
// Las anotaciones de validación rechazan la petición antes de que
// llegue a la base de datos, lo que previene datos basura y algunos ataques básicos.