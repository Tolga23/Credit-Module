package com.bank.app.infrastructure.adapters.out.persistence.loan.repository;

import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanInstallmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanInstallmentJpaRepository extends JpaRepository<LoanInstallmentEntity, Long> {
    List<LoanInstallmentEntity> findByLoanId(Long loanId);
}
