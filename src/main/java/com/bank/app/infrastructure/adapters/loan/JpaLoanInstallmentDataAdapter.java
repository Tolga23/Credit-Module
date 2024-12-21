package com.bank.app.infrastructure.adapters.loan;

import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.domain.port.LoanInstallmentPort;
import com.bank.app.infrastructure.adapters.loan.entity.LoanInstallmentEntity;
import com.bank.app.infrastructure.adapters.loan.mapper.LoanInstallmentEntityMapper;
import com.bank.app.infrastructure.adapters.loan.repository.LoanInstallmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JpaLoanInstallmentDataAdapter implements LoanInstallmentPort {
    private final LoanInstallmentJpaRepository repository;
    private final LoanInstallmentEntityMapper mapper;

    @Override
    public LoanInstallment save(LoanInstallment installment) {
        LoanInstallmentEntity entity = mapper.toEntity(installment);
        return mapper.toDomain(repository.save(entity));
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
    public void update(LoanInstallment installment) {
        repository.save(mapper.toEntity(installment));
    }
}
