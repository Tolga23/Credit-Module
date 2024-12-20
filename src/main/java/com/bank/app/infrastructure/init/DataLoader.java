package com.bank.app.infrastructure.init;

import com.bank.app.domain.model.Role;
import com.bank.app.domain.model.User;
import com.bank.app.domain.service.RoleService;
import com.bank.app.domain.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        Role adminRole = createRole("ROLE_ADMIN");
        Role customerRole = createRole("ROLE_CUSTOMER");

        // Initialize users
        createUser("admin", "admin", Set.of(adminRole, customerRole));
        createUser("customer", "customer", Set.of(customerRole));
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleService.save(role);
    }

    private void createUser(String username, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoleName(roles);
        userService.save(user);
    }
}
