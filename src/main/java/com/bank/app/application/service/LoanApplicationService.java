package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.domain.service.LoanDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanPort loanPort;
    private final CustomerPort customerPort;
    private final LoanDomainService loanDomainService;
    private final LoanInstallmentPort loanInstallmentPort;

    @Transactional
    public Loan createLoan(CreateLoanCommand request) {
        // Get customer
        Customer customer = customerPort.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Create new loan
        Loan loan = Loan.createNewLoan(
                request.customerId(),
                request.amount(),
                request.numberOfInstallments(),
                request.interestRate()
        );

        // Validate customer
        loanDomainService.validateCustomerCredit(customer, loan.getTotalLoanAmount());
        customerPort.update(customer);

        // Save loan
        Loan savedLoan = loanPort.save(loan);
        savedLoan.setInterestRate(request.interestRate());

        // Create and save installments
        List<LoanInstallment> installments = loanDomainService.createInstallment(savedLoan);
        installments.forEach(installment -> {
            LoanInstallment savedInstallment = loanInstallmentPort.save(installment);
            savedLoan.addInstallments(savedInstallment);
        });

        return savedLoan;
    }
}
