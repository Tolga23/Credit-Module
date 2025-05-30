package com.bank.app.infrastructure.adapters.out.persistence.customer.mapper;

import com.bank.app.domain.model.common.Money;
import com.bank.app.domain.model.customer.Customer;
import com.bank.app.infrastructure.adapters.out.persistence.customer.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerEntityMapper {
    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) return null;

        Customer customer = Customer.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .creditLimit(new Money(entity.getCreditLimit()))
                .usedCreditLimit(new Money(entity.getUsedCreditLimit()))
                .build();

        return customer;
    }

    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setSurname(domain.getSurname());
        entity.setCreditLimit(domain.getCreditLimit().getValue());
        entity.setUsedCreditLimit(domain.getUsedCreditLimit().getValue());
        return entity;
    }
}
