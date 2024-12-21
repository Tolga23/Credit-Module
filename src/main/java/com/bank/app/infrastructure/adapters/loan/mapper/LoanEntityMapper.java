package com.bank.app.infrastructure.adapters.loan.mapper;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.infrastructure.adapters.loan.entity.LoanEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanEntityMapper {

    private final LoanInstallmentEntityMapper installmentMapper;

    public Loan toDomain(LoanEntity entity) {
        if (entity == null) return null;

        Loan loan = Loan.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .loanAmount(entity.getLoanAmount())
                .numberOfInstallment(entity.getNumberOfInstallment())
                .createdDate(entity.getCreateDate())
                .isPaid(entity.isPaid())
                .build();

        if (entity.getInstallments() != null) {
            loan.setInstallments(entity.getInstallments().stream()
                    .map(installmentMapper::toDomain)
                    .collect(Collectors.toList()));
        }

        return loan;
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

        if (domain.getInstallments() != null) {
            entity.setInstallments(domain.getInstallments().stream()
                    .map(installmentMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
