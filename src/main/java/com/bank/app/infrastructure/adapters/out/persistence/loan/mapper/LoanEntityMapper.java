package com.bank.app.infrastructure.adapters.out.persistence.loan.mapper;

import com.bank.app.domain.model.loan.Loan;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanEntity;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanInstallmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
            List<LoanInstallmentEntity> installments = domain.getInstallments().stream()
                    .map(installmentMapper::toEntity)
                    .toList();

            entity.setInstallments(installments);
            for (LoanInstallmentEntity installment : installments) {
                installment.setLoan(entity);
            }
        }

        return entity;
    }
}
