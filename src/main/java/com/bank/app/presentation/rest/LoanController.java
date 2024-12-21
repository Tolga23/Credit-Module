package com.bank.app.presentation.rest;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import com.bank.app.presentation.dto.response.LoanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanApplicationService service;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanCommand request) {
        return ResponseEntity.ok(LoanResponse.from(service.createLoan(request)));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(
            @RequestParam Long customerId,
            @RequestParam(required = false) Integer numberOfInstallments,
            @RequestParam(required = false) Boolean isPaid) {
        return ResponseEntity.ok(
                LoanResponse.from(service.getCustomerLoans(customerId, numberOfInstallments, isPaid))
        );
    }
}
