package com.bank.app.infrastructure.adapters.in.rest;

import com.bank.app.application.service.LoanInstallmentApplicationService;
import com.bank.app.infrastructure.adapters.in.rest.dto.request.LoanInstallmentRequest;
import com.bank.app.infrastructure.adapters.in.rest.dto.response.LoanInstallmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/installment")
@RequiredArgsConstructor
public class LoanInstallmentController {
    private final LoanInstallmentApplicationService service;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #request.customerId == authentication.principal.customerId)")
    public ResponseEntity<List<LoanInstallmentResponse>> getInstallmentsByLoan(@Valid @RequestBody LoanInstallmentRequest request) {
        return ResponseEntity.ok(
                LoanInstallmentResponse.from(
                        service.getInstallmentsByLoan(request.customerId(), request.loanId())
                )
        );
    }
}
