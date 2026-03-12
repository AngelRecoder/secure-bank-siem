package com.angel.secure_bank.repository;

import com.angel.secure_bank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

// Interfaz que hereda todo el CRUD básico de JpaRepository.
// Spring genera el SQL automáticamente a partir del nombre del método,
// findByEmail se traduce a: SELECT * FROM users WHERE email = ?
// No necesitamos escribir más que esto para tener acceso completo a la tabla users.