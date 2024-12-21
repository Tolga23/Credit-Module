package com.bank.app.infrastructure.adapters.loan.mapper;

import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.infrastructure.adapters.loan.entity.LoanInstallmentEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanInstallmentEntityMapper {
    public LoanInstallment toDomain(LoanInstallmentEntity entity) {
        if (entity == null) return null;

        return LoanInstallment.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .paidAmount(entity.getPaidAmount())
                .dueDate(entity.getDueDate())
                .paymentDate(entity.getPaymentDate())
                .isPaid(entity.isPaid())
                .build();
    }

    public LoanInstallmentEntity toEntity(LoanInstallment domain) {
        if (domain == null) return null;

        LoanInstallmentEntity entity = new LoanInstallmentEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount());
        entity.setPaidAmount(domain.getPaidAmount());
        entity.setDueDate(domain.getDueDate());
        entity.setPaymentDate(domain.getPaymentDate());
        entity.setPaid(domain.isPaid());
        return entity;
    }
}
