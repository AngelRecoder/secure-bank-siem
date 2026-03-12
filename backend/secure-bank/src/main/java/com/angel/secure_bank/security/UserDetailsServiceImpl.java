package com.angel.secure_bank.security;

import com.angel.secure_bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.angel.secure_bank.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                user.isAccountNonLocked(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}

// Spring Security llama a este método automáticamente durante la autenticación.
// Buscamos al usuario por email en la base de datos y lo convertimos
// al formato que Spring Security entiende.
// El prefijo ROLE_ es requerido por Spring para el sistema de autorización por roles.
// Si el usuario no existe lanzamos UsernameNotFoundException,
// Spring Security la captura y devuelve 401 automáticamente.