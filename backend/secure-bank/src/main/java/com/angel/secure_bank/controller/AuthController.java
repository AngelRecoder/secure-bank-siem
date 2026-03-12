package com.angel.secure_bank.controller;

import com.angel.secure_bank.dto.AuthResponse;
import com.angel.secure_bank.dto.LoginRequest;
import com.angel.secure_bank.dto.RegisterRequest;
import com.angel.secure_bank.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

// Controlador con dos endpoints públicos, register y login.
// @Valid activa las validaciones que definimos en los DTOs,
// si el email tiene formato incorrecto o la contraseña es muy corta,
// Spring rechaza la petición antes de que llegue al servicio.
// @RequestBody deserializa el JSON que manda el frontend a nuestro DTO.
// ResponseEntity nos da control total sobre el código HTTP de respuesta.