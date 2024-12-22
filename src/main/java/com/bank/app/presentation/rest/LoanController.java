package com.bank.app.presentation.rest;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.LoanSearchCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import com.bank.app.presentation.dto.request.CreateLoanRequest;
import com.bank.app.presentation.dto.request.LoanSearchRequest;
import com.bank.app.presentation.dto.request.PayLoanRequest;
import com.bank.app.presentation.dto.response.LoanResponse;
import com.bank.app.presentation.dto.response.PayLoanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanApplicationService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #request.customerId == authentication.principal.customerId)")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        CreateLoanCommand command = CreateLoanCommand.from(request);
        return ResponseEntity.ok(LoanResponse.from(service.createLoan(command)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #request.customerId == authentication.principal.customerId)")
    public ResponseEntity<List<LoanResponse>> getCustomerLoans(
            @Valid @RequestBody LoanSearchRequest request) {
        LoanSearchCommand searchCommand = LoanSearchCommand.from(request);
        return ResponseEntity.ok(LoanResponse.from(service.getCustomerLoans(searchCommand)));
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #request.customerId == authentication.principal.customerId)")
    public ResponseEntity<PayLoanResponse> payLoan(@Valid @RequestBody PayLoanRequest request) {
        PayLoanCommand payLoanCommand = PayLoanCommand.from(request);
        return ResponseEntity.ok(
                service.payLoan(request.customerId(), payLoanCommand)
        );
    }
}
