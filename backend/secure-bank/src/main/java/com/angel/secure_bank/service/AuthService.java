package com.angel.secure_bank.service;

import com.angel.secure_bank.dto.AuthResponse;
import com.angel.secure_bank.dto.LoginRequest;
import com.angel.secure_bank.dto.RegisterRequest;
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
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, 86400, user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        return new AuthResponse(token, 86400, user.getRole().name());
    }
}

// Servicio que maneja registro y login.
// En registro verificamos primero si el email ya existe para dar un mensaje claro,
// luego hasheamos la contraseña con BCrypt antes de guardarla,
// nunca guardamos contraseñas en texto plano.
// En login delegamos la verificación de credenciales al AuthenticationManager
// que internamente usa BCrypt para comparar el hash almacenado con lo que llegó.
// Si las credenciales son incorrectas, AuthenticationManager lanza una excepción
// automáticamente y Spring Security devuelve 401 sin que tengamos que manejarlo.