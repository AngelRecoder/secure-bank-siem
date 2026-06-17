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
import org.springframework.security.authentication.BadCredentialsException;
import java.time.LocalDateTime;

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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // si la cuenta está bloqueada y ya pasó el tiempo, la desbloqueamos antes de seguir
        if (!user.isAccountNonLocked() && user.getLockedUntil() != null) {
            if (LocalDateTime.now().isAfter(user.getLockedUntil())) {
                user.setAccountNonLocked(true);
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (Exception ex) {
            handleFailedLogin(user, ipAddress, userAgent);
            throw ex;
        }

        // login exitoso, resetea el contador si tenía intentos previos
        if (user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        auditService.log(
                "LOGIN_SUCCESS",
                "User logged in: " + user.getEmail(),
                user, ipAddress, userAgent,
                AuditSeverity.INFO
        );

        return new AuthResponse(token, 86400, user.getRole().name());
    }

    private void handleFailedLogin(User user, String ipAddress, String userAgent) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));

            auditService.log(
                    "ACCOUNT_LOCKED",
                    "Account locked after 5 failed attempts: " + user.getEmail(),
                    user, ipAddress, userAgent,
                    AuditSeverity.CRITICAL
            );
        } else {
            auditService.log(
                    "LOGIN_FAILED",
                    "Failed login attempt " + attempts + "/5 for: " + user.getEmail(),
                    user, ipAddress, userAgent,
                    AuditSeverity.WARNING
            );
        }

        userRepository.save(user);
    }
}

// Ahora cada login exitoso, fallido y registro queda guardado en audit_logs.
// El login fallido es WARNING porque puede indicar un ataque de fuerza bruta.
// Pasamos ipAddress y userAgent desde el controlador para saber
// desde dónde vino cada intento.rity devuelve 401 sin que tengamos que manejarlo.