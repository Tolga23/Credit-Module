package com.bank.app.infrastructure.adapters.out.persistence.user.repository;

import com.bank.app.infrastructure.adapters.out.persistence.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
