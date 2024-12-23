package com.bank.app.infrastructure.adapters.out.persistence.loan;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.domain.port.LoanPort;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanEntity;
import com.bank.app.infrastructure.adapters.out.persistence.loan.mapper.LoanEntityMapper;
import com.bank.app.infrastructure.adapters.out.persistence.loan.repository.LoanJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanDataAdapter implements LoanPort {
    private final LoanJpaRepository repository;
    private final LoanEntityMapper mapper;

    @Transactional
    @Override
    public Loan save(Loan loan) {
        LoanEntity entity = mapper.toEntity(loan);
        LoanEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
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
    public List<Loan> findByCustomerIdAndFilters(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        return repository.findByCustomerIdAndFilters(customerId, numberOfInstallments, isPaid).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public void pay(Loan loan) {
        // Get existing entity with current installments
        LoanEntity existingEntity = repository.findById(loan.getId())
                .orElseThrow(() -> new IllegalStateException("Loan not found with ID: " + loan.getId()));

        existingEntity.setPaid(loan.isPaid());

        LoanEntity savedEntity = repository.save(existingEntity);

        mapper.toDomain(savedEntity);
    }
}
