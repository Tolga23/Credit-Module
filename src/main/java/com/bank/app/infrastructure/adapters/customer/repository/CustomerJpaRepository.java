package com.bank.app.infrastructure.adapters.customer.repository;

import com.bank.app.infrastructure.adapters.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
}
