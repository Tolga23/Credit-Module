package com.bank.app.presentation.rest;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.service.LoanApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loan")
public class LoanController {
    private final LoanApplicationService service;

    public LoanController(LoanApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity createLoan(@Valid @RequestBody CreateLoanCommand request) {
        return ResponseEntity.ok(service.createLoan(request));
    }
}
