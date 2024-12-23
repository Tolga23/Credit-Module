package com.bank.app.infrastructure.adapters.out.persistence.customer.repository;

import com.bank.app.infrastructure.adapters.out.persistence.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
}
