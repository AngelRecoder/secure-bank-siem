package com.angel.secure_bank.controller;

import com.angel.secure_bank.dto.AuthResponse;
import com.angel.secure_bank.dto.LoginRequest;
import com.angel.secure_bank.dto.RegisterRequest;
import com.angel.secure_bank.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        return ResponseEntity.ok(authService.login(request, ipAddress, userAgent));
    }
}

// HttpServletRequest nos da acceso a los datos de la petición HTTP,
// de ahí sacamos la IP y el User-Agent para guardarlos en el log de auditoría.