package com.angel.secure_bank.repository;

import com.angel.secure_bank.model.Transaction;
import com.angel.secure_bank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);
}

// Repositorio para transacciones.
// El método findBySender... puede parecer largo pero es la convención de Spring:
// busca por sender O receiver y ordena por fecha descendente.
// Esto nos dará el historial completo de movimientos de un usuario.