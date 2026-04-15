package com.angel.secure_bank.controller;

import com.angel.secure_bank.dto.UserProfileResponse;
import com.angel.secure_bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }
}

// @AuthenticationPrincipal le dice a Spring que inyecte automáticamente
// el usuario que está autenticado en ese momento.
// userDetails.getUsername() devuelve el email que guardamos en el token JWT.