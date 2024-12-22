package com.bank.app.presentation.rest;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import com.bank.app.infrastructure.security.domain.UserDetailsImpl;
import com.bank.app.presentation.dto.request.CreateLoanRequest;
import com.bank.app.presentation.dto.request.LoanSearchRequest;
import com.bank.app.presentation.dto.request.PayLoanRequest;
import com.bank.app.presentation.dto.response.LoanResponse;
import com.bank.app.presentation.dto.response.PayLoanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanApplicationService service;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        CreateLoanCommand command = CreateLoanCommand.from(request);
        return ResponseEntity.ok(LoanResponse.from(service.createLoan(command)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(
            @AuthenticationPrincipal UserDetailsImpl user,
            @Valid @RequestBody(required = false) LoanSearchRequest request) {
        List<LoanResponse> responses = LoanResponse.from(
                service.getCustomerLoans(
                        user.getCustomerId(), request.numberOfInstallments(), request.isPaid())
        );
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/pay")
    public ResponseEntity<PayLoanResponse> payLoan(@AuthenticationPrincipal UserDetailsImpl user,
                                                   @Valid @RequestBody PayLoanRequest request) {
        PayLoanCommand payLoanCommand = PayLoanCommand.from(request);
        return ResponseEntity.ok(
                service.payLoan(user.getCustomerId(), payLoanCommand)
        );
    }
}
