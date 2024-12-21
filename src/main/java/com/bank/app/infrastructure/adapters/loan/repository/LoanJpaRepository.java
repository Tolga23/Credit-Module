package com.bank.app.infrastructure.adapters.loan.repository;

import com.bank.app.infrastructure.adapters.loan.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanJpaRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByCustomerId(Long customerId);
}
