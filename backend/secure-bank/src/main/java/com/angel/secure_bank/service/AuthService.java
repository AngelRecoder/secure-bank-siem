package com.angel.secure_bank.service;

import com.angel.secure_bank.dto.AuthResponse;
import com.angel.secure_bank.dto.LoginRequest;
import com.angel.secure_bank.dto.RegisterRequest;
import com.angel.secure_bank.model.AuditSeverity;
import com.angel.secure_bank.model.Role;
import com.angel.secure_bank.model.User;
import com.angel.secure_bank.repository.UserRepository;
import com.angel.secure_bank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AuditService auditService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(user);

        // registramos el evento de registro nuevo
        auditService.log(
                "USER_REGISTERED",
                "New user registered: " + user.getEmail(),
                user, null, null,
                AuditSeverity.INFO
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, 86400, user.getRole().name());
    }

    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception ex) {
            // login fallido, lo registramos como advertencia
            userRepository.findByEmail(request.email()).ifPresent(user ->
                    auditService.log(
                            "LOGIN_FAILED",
                            "Failed login attempt for: " + request.email(),
                            user, ipAddress, userAgent,
                            AuditSeverity.WARNING
                    )
            );
            throw ex;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        // login exitoso
        auditService.log(
                "LOGIN_SUCCESS",
                "User logged in: " + user.getEmail(),
                user, ipAddress, userAgent,
                AuditSeverity.INFO
        );

        return new AuthResponse(token, 86400, user.getRole().name());
    }
}

// Ahora cada login exitoso, fallido y registro queda guardado en audit_logs.
// El login fallido es WARNING porque puede indicar un ataque de fuerza bruta.
// Pasamos ipAddress y userAgent desde el controlador para saber
// desde dónde vino cada intento.rity devuelve 401 sin que tengamos que manejarlo.