package com.angel.secure_bank.controller;

import com.angel.secure_bank.dto.TransferRequest;
import com.angel.secure_bank.dto.TransferResponse;
import com.angel.secure_bank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(
                transactionService.transfer(userDetails.getUsername(), request)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransferResponse>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                transactionService.getHistory(userDetails.getUsername())
        );
    }
}

// Dos endpoints: uno para transferir y otro para ver el historial.
// En ambos casos el email del usuario autenticado viene del token,
// no de parámetros que el usuario pueda manipular.