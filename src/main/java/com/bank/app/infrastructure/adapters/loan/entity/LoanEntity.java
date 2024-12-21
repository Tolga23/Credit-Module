package com.bank.app.infrastructure.adapters.loan.entity;

import com.bank.app.infrastructure.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan")
@Getter
@Setter
public class LoanEntity extends BaseEntity {
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private BigDecimal loanAmount;

    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallment;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;
}
