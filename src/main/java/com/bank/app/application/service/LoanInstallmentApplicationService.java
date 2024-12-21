package com.bank.app.application.service;

import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanInstallmentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanInstallmentApplicationService {
    private final LoanInstallmentPort installmentPort;

    public List<LoanInstallment> getInstallmentsByLoan(Long loanId) {
        return installmentPort.findByLoanId(loanId);
    }
}
