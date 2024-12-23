package com.bank.app.infrastructure.adapters.out.persistence.loan.repository;

import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanJpaRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByCustomerId(Long customerId);

    @Query("SELECT l FROM LoanEntity l WHERE l.customerId = :customerId " +
            "AND (:numberOfInstallments IS NULL OR l.numberOfInstallment = :numberOfInstallments) " +
            "AND (:isPaid IS NULL OR l.isPaid = :isPaid)")
    List<LoanEntity> findByCustomerIdAndFilters(
            @Param("customerId") Long customerId,
            @Param("numberOfInstallments") Integer numberOfInstallments,
            @Param("isPaid") Boolean isPaid
    );
}
