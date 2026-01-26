package com.nimis.chatbot.config;

import com.nimis.chatbot.model.entity.RoleEntity;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.repository.RoleRepository;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedSuperAdmin();
    }

    // ==========================
    // Seed Roles
    // ==========================
    private void seedRoles() {
        seedRole("ROLE_SUPER_ADMIN");
        seedRole("ROLE_BANK_ADMIN");
        seedRole("ROLE_VENDOR_ADMIN");
        seedRole("ROLE_FO");
    }

    private void seedRole(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() ->
                        roleRepository.save(
                                RoleEntity.builder()
                                        .name(roleName)
                                        .build()
                        )
                );
    }

    // ==========================
    // Seed Super Admin
    // ==========================
    private void seedSuperAdmin() {

        final String email = "platform@admin.com";
        final String username = "superadmin";

        if (userRepository.existsByEmail(email)) {
            System.out.println("ℹ️ SUPER_ADMIN already exists");
            return;
        }

        RoleEntity superAdminRole = roleRepository
                .findByName("ROLE_SUPER_ADMIN")
                .orElseThrow(() ->
                        new IllegalStateException("ROLE_SUPER_ADMIN not found")
                );

        UserEntity superAdmin = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode("SuperAdmin@123"))
                .email(email)
                .firstName("Super")
                .lastName("Admin")
                .phone("9999999999")
                .location("HQ")
                .organization("AD")
                .roles(Set.of(superAdminRole))
                .enabled(true)
                .bank(null)
                .vendor(null)
                .build();

        userRepository.save(superAdmin);

        System.out.println("    SUPER_ADMIN seeded successfully");
        System.out.println("    username: superadmin");
        System.out.println("    password: SuperAdmin@123");
    }
}
