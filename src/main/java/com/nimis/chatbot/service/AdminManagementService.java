package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.request.*;
import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.mapper.AppMapper;
import com.nimis.chatbot.model.*;
import com.nimis.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final BankRepository bankRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AppMapper mapper;

    // =========================
    // SUPER ADMIN OPERATIONS
    // =========================
    @Transactional
    public BankResponseDTO createBank(CreateBankRequest request) {
        if (bankRepository.findByName(request.getBankName()).isPresent()) {
            throw new IllegalArgumentException("Bank already exists");
        }
        BankEntity bank = BankEntity.builder()
                .name(request.getBankName())
                .build();
        return mapper.toDTO(bankRepository.save(bank));
    }

    @Transactional
    public UserResponseDTO createBankAdmin(CreateBankAdminRequest request) {
        BankEntity bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        RoleEntity role = roleRepository.findByName("ROLE_BANK_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_BANK_ADMIN not found"));

        UserEntity admin = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .bank(bank)
                .vendor(null)
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return mapper.toDTO(userRepository.save(admin));
    }

    // =========================
    // BANK ADMIN OPERATIONS
    // =========================
    @Transactional
    public VendorResponseDTO createCompany(CreateVendorRequest request) {
        UserEntity current = authService.getCurrentUserEntity();
        if (!hasRole(current, "ROLE_BANK_ADMIN")) {
            throw new AccessDeniedException("BANK_ADMIN access required");
        }

        if (vendorRepository.findByNameAndBankId(request.getName(), current.getBank().getId()).isPresent()) {
            throw new IllegalArgumentException("Vendor already exists for this bank");
        }

        VendorEntity vendor = VendorEntity.builder()
                .name(request.getName())
                .bank(current.getBank())
                .build();

        return mapper.toDTO(vendorRepository.save(vendor));
    }

    @Transactional
    public UserResponseDTO createVendorAdmin(CreateVendorAdminRequest request) {
        UserEntity current = authService.getCurrentUserEntity();
        if (!hasRole(current, "ROLE_BANK_ADMIN")) {
            throw new AccessDeniedException("BANK_ADMIN access required");
        }

        VendorEntity vendor = vendorRepository.findByIdAndBankId(request.getVendorId(), current.getBank().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        RoleEntity role = roleRepository.findByName("ROLE_VENDOR_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_VENDOR_ADMIN missing"));

        UserEntity admin = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .bank(current.getBank())
                .vendor(vendor)
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return mapper.toDTO(userRepository.save(admin));
    }

    // =========================
    // VENDOR ADMIN OPERATIONS
    // =========================
    @Transactional
    public UserResponseDTO createFO(CreateFORequest request) {
        UserEntity current = authService.getCurrentUserEntity();
        if (!hasRole(current, "ROLE_VENDOR_ADMIN")) {
            throw new AccessDeniedException("VENDOR_ADMIN access required");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        RoleEntity role = roleRepository.findByName("ROLE_FO")
                .orElseThrow(() -> new IllegalStateException("ROLE_FO missing"));

        UserEntity fo = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .bank(current.getBank())
                .vendor(current.getVendor())
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return mapper.toDTO(userRepository.save(fo));
    }

    // =========================
    // UTIL
    // =========================
    private boolean hasRole(UserEntity user, String role) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(role));
    }
}
