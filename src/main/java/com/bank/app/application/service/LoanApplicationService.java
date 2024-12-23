package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.LoanSearchCommand;
import com.bank.app.application.command.PayLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.domain.service.LoanCreateService;
import com.bank.app.domain.service.LoanPaymentService;
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
    private final LoanCreateService loanCreateService;
    private final LoanInstallmentPort loanInstallmentPort;
    private final LoanPaymentService loanPaymentService;

    @Transactional
    public Loan createLoan(CreateLoanCommand request) {
        Customer customer = customerPort.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Loan loan = loanCreateService.createLoan(customer, request);
        // Customer usedCreditLimit update
        customerPort.update(customer);

        // Save loan to get id
        Loan savedLoan = loanPort.save(loan);
        if (savedLoan == null) throw new IllegalStateException("Loan couldn't be saved");

        savedLoan.setInterestRate(request.interestRate());

        createAndSaveInstallments(savedLoan);

        return savedLoan;
    }

    @Transactional
    public PayLoanResponse payLoan(PayLoanCommand command) {
        // Check if loan exists
        Loan loan = loanPort.findById(command.loanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        loanPaymentService.validateOwnership(loan, command.customerId());

        // Process payment
        PaymentResult result = loanPaymentService.processPayment(loan, command.amount());

        // Save changes if payment was successful
        if (result.totalPaid().compareTo(BigDecimal.ZERO) > 0) {
            // Update loan installments
            loan.getInstallments().stream()
                    .filter(LoanInstallment::isPaid)
                    .forEach(loanInstallmentPort::update);

            // Update loan status
            loanPort.pay(loan);

            // Update customer credit
            updateCustomerCredit(command.customerId(), result.originalAmount());
        }

        return new PayLoanResponse(result.totalPaid(), result.installmentsPaid(), loan.isPaid());
    }

    private void updateCustomerCredit(Long customerId, BigDecimal originalAmount) {
        Customer customer = customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // original amount of added because of the penalty/discount.
        customer.releaseCredit(originalAmount);
        customerPort.update(customer);
    }

    private void createAndSaveInstallments(Loan savedLoan) {
        // Create and save installments
        List<LoanInstallment> installments = loanCreateService.createInstallment(savedLoan);
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
