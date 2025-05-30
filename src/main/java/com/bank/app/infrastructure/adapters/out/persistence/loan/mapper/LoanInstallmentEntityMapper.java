package com.bank.app.infrastructure.adapters.out.persistence.loan.mapper;

import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.loan.LoanInstallment;
import com.bank.app.infrastructure.adapters.out.persistence.loan.entity.LoanInstallmentEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanInstallmentEntityMapper {
    public LoanInstallment toDomain(LoanInstallmentEntity entity) {
        if (entity == null) return null;

        return LoanInstallment.builder()
                .id(entity.getId())
                .loanId(entity.getLoan().getId())
                .amount(new Money(entity.getAmount()))
                .paidAmount(new Money(entity.getPaidAmount()))
                .dueDate(entity.getDueDate())
                .paymentDate(entity.getPaymentDate())
                .isPaid(entity.isPaid())
                .build();
    }

    public LoanInstallmentEntity toEntity(LoanInstallment domain) {
        if (domain == null) return null;

        LoanInstallmentEntity entity = new LoanInstallmentEntity();
        entity.setId(domain.getId());
        entity.setAmount(domain.getAmount().getValue());
        entity.setPaidAmount(domain.getPaidAmount().getValue());
        entity.setDueDate(domain.getDueDate());
        entity.setPaymentDate(domain.getPaymentDate());
        entity.setPaid(domain.isPaid());
        return entity;
    }
}
