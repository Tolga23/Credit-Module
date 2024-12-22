package com.bank.app.presentation.rest;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import com.bank.app.infrastructure.security.domain.UserDetailsImpl;
import com.bank.app.presentation.dto.response.LoanResponse;
import com.bank.app.presentation.dto.response.PayLoanResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam(required = false) Integer numberOfInstallments,
            @RequestParam(required = false) Boolean isPaid) {
        List<LoanResponse> responses = LoanResponse.from(
                service.getCustomerLoans(user.getCustomerId(), numberOfInstallments, isPaid));
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/pay")
    public ResponseEntity<PayLoanResponse> payLoan(@AuthenticationPrincipal UserDetailsImpl user,
                                                   @RequestParam Long loanId,
                                                   @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(
                service.payLoan(user.getCustomerId(), loanId, amount)
        );
    }
}
