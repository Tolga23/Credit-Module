package com.bank.app.presentation.rest;

import com.bank.app.application.service.LoanInstallmentApplicationService;
import com.bank.app.presentation.dto.response.LoanInstallmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/loan/installment")
@RequiredArgsConstructor
public class LoanInstallmentController {
    private final LoanInstallmentApplicationService service;

    @GetMapping()
    public ResponseEntity<List<LoanInstallmentResponse>> getInstallmentsByLoanId(@RequestParam Long loanId) {
        return ResponseEntity.ok(
                LoanInstallmentResponse.from(service.getInstallmentsByLoan(loanId))
        );
    }
}
