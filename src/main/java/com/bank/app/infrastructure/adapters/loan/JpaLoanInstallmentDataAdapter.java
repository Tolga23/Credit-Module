package com.bank.app.infrastructure.adapters.loan;

import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.infrastructure.adapters.loan.entity.LoanEntity;
import com.bank.app.infrastructure.adapters.loan.entity.LoanInstallmentEntity;
import com.bank.app.infrastructure.adapters.loan.mapper.LoanInstallmentEntityMapper;
import com.bank.app.infrastructure.adapters.loan.repository.LoanInstallmentJpaRepository;
import com.bank.app.infrastructure.adapters.loan.repository.LoanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaLoanInstallmentDataAdapter implements LoanInstallmentPort {
    private final LoanInstallmentJpaRepository repository;
    private final LoanJpaRepository loanRepository;
    private final LoanInstallmentEntityMapper mapper;

    @Override
    @Transactional
    public LoanInstallment save(LoanInstallment installment) {
        // Get loan entity
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
    public void update(LoanInstallment installment) {
        repository.save(mapper.toEntity(installment));
    }
}
