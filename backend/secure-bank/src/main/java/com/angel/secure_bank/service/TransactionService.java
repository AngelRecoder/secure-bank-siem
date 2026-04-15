package com.angel.secure_bank.service;

import com.angel.secure_bank.dto.TransferRequest;
import com.angel.secure_bank.dto.TransferResponse;
import com.angel.secure_bank.model.Transaction;
import com.angel.secure_bank.model.TransactionStatus;
import com.angel.secure_bank.model.User;
import com.angel.secure_bank.repository.TransactionRepository;
import com.angel.secure_bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransferResponse transfer(String senderEmail, TransferRequest request) {
        if (senderEmail.equals(request.recipientEmail())) {
            throw new IllegalArgumentException("You cannot transfer money to yourself");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));

        User recipient = userRepository.findByEmail(request.recipientEmail())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(request.amount()));
        recipient.setBalance(recipient.getBalance().add(request.amount()));

        userRepository.save(sender);
        userRepository.save(recipient);

        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(recipient)
                .amount(request.amount())
                .status(TransactionStatus.COMPLETED)
                .description(request.description())
                .build();

        Transaction saved = transactionRepository.save(transaction);

        return new TransferResponse(
                saved.getId(),
                sender.getEmail(),
                recipient.getEmail(),
                saved.getAmount(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );
    }

    public List<TransferResponse> getHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return transactionRepository
                .findBySenderOrReceiverOrderByCreatedAtDesc(user, user)
                .stream()
                .map(t -> new TransferResponse(
                        t.getId(),
                        t.getSender().getEmail(),
                        t.getReceiver().getEmail(),
                        t.getAmount(),
                        t.getStatus().name(),
                        t.getCreatedAt()
                ))
                .toList();
    }
}

// @Transactional es importante aquí, si algo falla a mitad de la transferencia
// (por ejemplo se descuenta del sender pero falla al acreditar al recipient)
// la base de datos revierte todo automáticamente y nadie pierde dinero.
// El senderEmail viene del token JWT, no del body de la petición,
// así un usuario no puede hacer transferencias haciéndose pasar por otro.