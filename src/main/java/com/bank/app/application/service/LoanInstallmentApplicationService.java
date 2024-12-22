package com.bank.app.application.service;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.domain.port.LoanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanInstallmentApplicationService {
    private final LoanInstallmentPort installmentPort;
    private final LoanPort loanPort;

    public List<LoanInstallment> getInstallmentsByLoan(Long customerId, Long loanId) {
        validateLoanOwnership(loanId, customerId);
        return installmentPort.findByLoanId(loanId);
    }

    private void validateLoanOwnership(Long loanId, Long customerId) {
        Loan loan = loanPort.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!loan.getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You do not have permission to access this loan's installments");
        }
    }
}
