package com.bank.app.infrastructure.persistence.mapper;

import com.bank.app.domain.model.user.User;
import com.bank.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .role(entity.getRole())
                .customerId(entity.getCustomerId())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setCustomerId(domain.getCustomerId());
        return entity;
    }
}
