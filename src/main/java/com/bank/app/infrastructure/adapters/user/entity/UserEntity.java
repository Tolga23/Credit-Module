package com.bank.app.infrastructure.adapters.user.entity;

import com.bank.app.domain.model.user.UserRole;
import com.bank.app.infrastructure.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "customer_id")
    private Long customerId;
}
