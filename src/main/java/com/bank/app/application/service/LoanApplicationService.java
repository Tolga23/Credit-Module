package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.domain.service.LoanDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanPort loanPort;
    private final CustomerPort customerPort;
    private final LoanDomainService loanDomainService;

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

        // Create installments
        loanDomainService.createInstallment(loan)
                .forEach(loan::addInstallments);

        return loanPort.save(loan);
    }
}
