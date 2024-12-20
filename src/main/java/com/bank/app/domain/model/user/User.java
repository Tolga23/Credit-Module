package com.bank.app.domain.model.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private Long customerId;

}
