package com.angel.secure_bank.service;

import com.angel.secure_bank.dto.UserProfileResponse;
import com.angel.secure_bank.model.User;
import com.angel.secure_bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBalance(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}

// Busca al usuario por email y devuelve solo los datos del perfil.
// El email lo sacamos del token JWT, no del body de la petición,
// así un usuario no puede pedir el perfil de otro mandando otro email.