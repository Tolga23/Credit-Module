package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.LoanSearchCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.*;
import com.bank.app.infrastructure.adapters.in.rest.dto.response.PayLoanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    private final LoanPort loanPort;
    private final CustomerPort customerPort;
    private final LoanCreatePort loanCreatePort;
    private final LoanInstallmentPort loanInstallmentPort;
    private final LoanPaymentPort loanPaymentPort;

    @Transactional
    public Loan createLoan(CreateLoanCommand request) {
        Customer customer = customerPort.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Loan loan = loanCreatePort.createLoan(customer, request);
        // Customer usedCreditLimit update
        customerPort.update(customer);

        // Save loan to get id
        loan = loanPort.save(loan);
        if (loan == null) throw new IllegalStateException("Loan couldn't be saved");

        // maybe it can be Transient in LoanEntity?
        loan.setInterestRate(request.interestRate());

        createAndSaveInstallments(loan);

        return loan;
    }

    @Transactional
    public PayLoanResponse payLoan(PayLoanCommand command) {
        // Check if loan exists
        Loan loan = loanPort.findById(command.loanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        // Process payment
        PaymentResult result = loanPaymentPort.processPayment(loan, command);

        // Save changes if payment was successful
        if (result.totalPaid().compareTo(Money.ZERO) > 0) {
            // Update loan installments
            loan.getInstallments().stream()
                    .filter(LoanInstallment::isPaid)
                    .forEach(loanInstallmentPort::update);

            // Update loan status
            loanPort.pay(loan);

            // Update customer credit
            updateCustomerCredit(command.customerId(), result.originalAmount());
        }

        return new PayLoanResponse(result.totalPaid().getValue(), result.installmentsPaid(), loan.isPaid());
    }

    private void updateCustomerCredit(Long customerId, Money originalAmount) {
        Customer customer = customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // original amount of added because of the penalty/discount.
        customer.releaseCredit(originalAmount);
        customerPort.update(customer);
    }

    private void createAndSaveInstallments(Loan savedLoan) {
        // Create and save installments
        List<LoanInstallment> installments = loanCreatePort.createInstallment(savedLoan);
        installments.forEach(installment -> {
            LoanInstallment savedInstallment = loanInstallmentPort.save(installment);
            if (savedInstallment == null) {
                throw new IllegalStateException("Failed to save loan installment");
            }
            savedLoan.addInstallments(savedInstallment);
        });
    }

    public List<Loan> getCustomerLoans(LoanSearchCommand command) {
        // Validate customer exists
        customerPort.findById(command.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return loanPort.findByCustomerIdAndFilters(
                command.customerId(),
                command.numberOfInstallments(),
                command.isPaid());
    }
}
