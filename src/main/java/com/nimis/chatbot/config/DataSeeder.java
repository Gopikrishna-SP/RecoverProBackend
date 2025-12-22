package com.nimis.chatbot.config;

import com.nimis.chatbot.model.RoleEntity;
import com.nimis.chatbot.model.UserEntity;
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
        seedRoles();           // Seed all roles
        seedSuperAdminUser();  // Seed only Super Admin
    }

    // ==========================
    // Seed all roles
    // ==========================
    private void seedRoles() {
        createRoleIfNotExists("ROLE_SUPER_ADMIN");
        createRoleIfNotExists("ROLE_BANK_ADMIN");
        createRoleIfNotExists("ROLE_VENDOR_ADMIN");
        createRoleIfNotExists("ROLE_FO");
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder()
                                .name(roleName)
                                .build()
                ));
    }

    // ==========================
    // Seed Super Admin user
    // ==========================
    private void seedSuperAdminUser() {
        if (userRepository.existsByEmail("platform@admin.com")) return;

        RoleEntity superAdminRole = roleRepository
                .findByName("ROLE_SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException(
                        "ROLE_SUPER_ADMIN not seeded!"
                ));

        UserEntity superAdmin = UserEntity.builder()
                .username("superadmin")
                .password(passwordEncoder.encode("SuperAdmin@123"))
                .email("platform@admin.com")
                .roles(Set.of(superAdminRole))
                .enabled(true)
                .bank(null)
                .vendor(null)
                .build();

        userRepository.save(superAdmin);
        System.out.println("✅ SUPER_ADMIN seeded successfully");
    }
}
