package com.bank.app.application.service;

import com.bank.app.application.command.CreateLoanCommand;
import com.bank.app.application.command.PaymentResult;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.CustomerPort;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.domain.service.LoanDomainService;
import com.bank.app.presentation.dto.response.PayLoanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {
    public static final int MAX_PAYMENT_MONTHS = 3;
    private final LoanPort loanPort;
    private final CustomerPort customerPort;
    private final LoanDomainService loanDomainService;
    private final LoanInstallmentPort loanInstallmentPort;

    @Transactional
    public Loan createLoan(CreateLoanCommand request) {
        Customer customer = validateAndGetCustomer(request.customerId());

        Loan loan = initializeLoan(request, customer);

        // Save loan to get id
        Loan savedLoan = loanPort.save(loan);
        savedLoan.setInterestRate(request.interestRate());

        createAndSaveInstallments(savedLoan);

        return savedLoan;
    }

    @Transactional
    public PayLoanResponse payLoan(Long customerId, Long loanId, BigDecimal amount) {
        Loan loan = fetchAndValidateLoan(customerId, loanId);

        List<LoanInstallment> eligibleInstallments = getEligibleInstallments(loanId);

        PaymentResult result = processPayment(amount, eligibleInstallments);

        // Update loan status if any payment was made
        if (result.totalPaid().compareTo(BigDecimal.ZERO) > 0) {
            updateLoanStatus(loanId, loan);
            updateCustomerCredit(customerId, result);
        }

        // Return payment result
        return new PayLoanResponse(result.totalPaid(), result.installmentsPaid(), loan.isPaid());
    }

    private void updateCustomerCredit(Long customerId, PaymentResult result) {
        // Update customer credit limit
        Customer customer = customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customer.releaseCredit(result.totalPaid());
        customerPort.update(customer);
    }

    private Loan updateLoanStatus(Long loanId, Loan loan) {
        // Check if all installments are paid
        boolean isFullyPaid = loanInstallmentPort.findByLoanId(loanId).stream()
                .allMatch(LoanInstallment::isPaid);

        loan.setPaid(isFullyPaid);
        loanPort.pay(loan); // Save the updated loan status
        return loan;
    }

    private Loan fetchAndValidateLoan(Long customerId, Long loanId) {
        // Fetch and validate loan
        Loan loan = loanPort.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        validateOwnership(loan, customerId);
        return loan;
    }

    private List<LoanInstallment> getEligibleInstallments(Long loanId) {
        // Get installments eligible for payment
        List<LoanInstallment> installments = loanInstallmentPort.findByLoanId(loanId).stream()
                .filter(installment -> !installment.isPaid()) // Only unpaid installments
                .filter(installment -> installment.getDueDate().isBefore(LocalDate.now().plusMonths(MAX_PAYMENT_MONTHS)))
                .sorted(Comparator.comparing(LoanInstallment::getDueDate)) // Sort by earliest due date
                .toList();

        if (installments.isEmpty()) {
            throw new IllegalStateException("No eligible installments found");
        }
        return installments;
    }

    private PaymentResult processPayment(BigDecimal amount, List<LoanInstallment> installments) {
        BigDecimal remainingAmount = amount;
        BigDecimal totalPaid = BigDecimal.ZERO;
        int installmentsPaid = 0;

        for (LoanInstallment installment : installments) {
            if (remainingAmount.compareTo(installment.getAmount()) >= 0) {
                // Full payment
                installment.setPaid(true);
                installment.setPaidAmount(installment.getAmount());
                remainingAmount = remainingAmount.subtract(installment.getAmount());
                totalPaid = totalPaid.add(installment.getAmount());
                installmentsPaid++;
                loanInstallmentPort.update(installment);
            } else if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Partial payment
                installment.setPaidAmount(installment.getAmount().subtract(remainingAmount));
                totalPaid = totalPaid.add(remainingAmount);
                remainingAmount = BigDecimal.ZERO;
                loanInstallmentPort.update(installment);
            } else {
                break;
            }
        }

        return new PaymentResult(totalPaid, installmentsPaid);
    }

    private void validateOwnership(Loan loan, Long customerId) {
        if (!loan.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Loan does not belong to the customer");
        }
    }

    private Customer validateAndGetCustomer(Long customerId) {
        return customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    private Loan initializeLoan(CreateLoanCommand request, Customer customer) {
        // Create new loan
        Loan loan = Loan.createNewLoan(
                request.customerId(),
                request.amount(),
                request.numberOfInstallments(),
                request.interestRate()
        );

        // Validate customer customer credit and update customer
        loanDomainService.validateAndUpdateCustomerCredit(customer, loan.getTotalLoanAmount());
        customerPort.update(customer);
        return loan;
    }

    private void createAndSaveInstallments(Loan savedLoan) {
        // Create and save installments
        List<LoanInstallment> installments = loanDomainService.createInstallment(savedLoan);
        installments.forEach(installment -> {
            LoanInstallment savedInstallment = loanInstallmentPort.save(installment);
            savedLoan.addInstallments(savedInstallment);
        });
    }

    public List<Loan> getCustomerLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        // Validate customer exists
        customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return loanPort.findByCustomerIdAndFilters(customerId, numberOfInstallments, isPaid);
    }
}
