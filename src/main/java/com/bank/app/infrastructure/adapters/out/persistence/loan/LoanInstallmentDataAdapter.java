package com.bank.app.infrastructure.adapters.out.persistence.loan;

import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanEntity;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanInstallmentEntity;
import com.bank.app.infrastructure.adapters.out.persistence.loan.mapper.LoanInstallmentEntityMapper;
import com.bank.app.infrastructure.adapters.out.persistence.loan.repository.LoanInstallmentJpaRepository;
import com.bank.app.infrastructure.adapters.out.persistence.loan.repository.LoanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanInstallmentDataAdapter implements LoanInstallmentPort {
    private final LoanInstallmentJpaRepository repository;
    private final LoanJpaRepository loanRepository;
    private final LoanInstallmentEntityMapper mapper;

    @Override
    @Transactional
    public LoanInstallment save(LoanInstallment installment) {
        LoanEntity loanEntity = loanRepository.findById(installment.getLoanId())
                .orElseThrow(() -> new IllegalStateException("Loan not found with ID: " + installment.getLoanId()));

        LoanInstallmentEntity entity = mapper.toEntity(installment);
        entity.setLoan(loanEntity);

        LoanInstallmentEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<LoanInstallment> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<LoanInstallment> findByLoanId(Long loanId) {
        return repository.findByLoanId(loanId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public LoanInstallment update(LoanInstallment installment) {
        LoanEntity loanEntity = loanRepository.findById(installment.getLoanId())
                .orElseThrow(() -> new IllegalStateException("Loan not found with ID: " + installment.getLoanId()));

        // Get existing entity
        LoanInstallmentEntity existingEntity = repository.findById(installment.getId())
                .orElseThrow(() -> new IllegalStateException("Installment not found with ID: " + installment.getId()));

        // Update fields
        existingEntity.setAmount(installment.getAmount());
        existingEntity.setPaid(installment.isPaid());
        existingEntity.setDueDate(installment.getDueDate());
        existingEntity.setPaidAmount(installment.getPaidAmount());
        existingEntity.setLoan(loanEntity);

        // Set payment date only if fully paid
        if (installment.isPaid()) {
            existingEntity.setPaymentDate(LocalDate.now());
        }

        LoanInstallmentEntity savedEntity = repository.save(existingEntity);
        return mapper.toDomain(savedEntity);
    }
}
