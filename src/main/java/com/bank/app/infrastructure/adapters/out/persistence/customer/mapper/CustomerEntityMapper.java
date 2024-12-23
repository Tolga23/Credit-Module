package com.bank.app.infrastructure.adapters.out.persistence.customer.mapper;

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
                .creditLimit(entity.getCreditLimit())
                .usedCreditLimit(entity.getUsedCreditLimit())
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
        entity.setCreditLimit(domain.getCreditLimit());
        entity.setUsedCreditLimit(domain.getUsedCreditLimit());
        return entity;
    }
}
