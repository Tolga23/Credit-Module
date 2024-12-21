package com.bank.app.infrastructure.adapters.loan.mapper;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.infrastructure.adapters.loan.entity.LoanEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanEntityMapper {
    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;

        return Loan.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .loanAmount(entity.getLoanAmount())
                .numberOfInstallment(entity.getNumberOfInstallment())
                .createdDate(entity.getCreateDate())
                .isPaid(entity.isPaid())
                .build();
    }

    public LoanEntity toEntity(Loan domain) {
        if (domain == null) return null;

        LoanEntity entity = new LoanEntity();
        entity.setId(domain.getId());
        entity.setCustomerId(domain.getCustomerId());
        entity.setLoanAmount(domain.getLoanAmount());
        entity.setNumberOfInstallment(domain.getNumberOfInstallment());
        entity.setCreateDate(domain.getCreatedDate());
        entity.setPaid(domain.isPaid());

        return entity;
    }
}
