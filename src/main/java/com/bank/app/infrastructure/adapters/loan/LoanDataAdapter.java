package com.bank.app.infrastructure.adapters.loan;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.infrastructure.adapters.loan.entity.LoanEntity;
import com.bank.app.infrastructure.adapters.loan.mapper.LoanEntityMapper;
import com.bank.app.infrastructure.adapters.loan.repository.LoanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanDataAdapter implements LoanPort {
    private final LoanJpaRepository repository;
    private final LoanEntityMapper mapper;

    @Override
    public Loan save(Loan loan) {
        LoanEntity entity = mapper.toEntity(loan);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Loan> findByCustomerId(Long customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void update(Loan loan) {
        repository.save(mapper.toEntity(loan));
    }
}