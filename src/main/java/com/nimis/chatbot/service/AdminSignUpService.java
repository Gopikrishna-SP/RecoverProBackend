package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.request.*;
import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.model.entity.BankEntity;
import com.nimis.chatbot.model.entity.RoleEntity;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.model.entity.VendorEntity;
import com.nimis.chatbot.utility.AppMapper;
import com.nimis.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminSignUpService {

    private final BankRepository bankRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AppMapper mapper;

    // =========================
    // SUPER ADMIN: CREATE BANK
    // =========================
    @Transactional
    public BankResponse createBank(CreateBankRequest request) {
        if (bankRepository.findByName(request.getBankName()).isPresent()) {
            throw new IllegalArgumentException("Bank already exists");
        }

        BankEntity bank = BankEntity.builder()
                .name(request.getBankName())
                .build();

        BankEntity savedBank = bankRepository.save(bank);
        return mapper.toDTO(savedBank);
    }

    // =========================
    // SUPER ADMIN: CREATE BANK ADMIN
    // =========================
    @Transactional
    public UserResponse createBankAdmin(CreateBankAdminRequest request) {
        BankEntity bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        RoleEntity role = roleRepository.findByName("ROLE_BANK_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_BANK_ADMIN not found"));

        UserEntity admin = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .location(request.getLocation())
                .organization(request.getOrganization())
                .bank(bank)
                .vendor(null)
                .roles(Set.of(role))
                .enabled(true)
                .build();

        UserEntity savedAdmin = userRepository.save(admin);
        return mapper.toDTO(savedAdmin);
    }

    // =========================
    // SUPER ADMIN: CREATE VENDOR
    // =========================
    @Transactional
    public VendorResponse createVendor(CreateVendorRequest request) {
        BankEntity bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        if (vendorRepository.findByNameAndBankId(request.getName(), request.getBankId()).isPresent()) {
            throw new IllegalArgumentException("Vendor already exists for this bank");
        }

        VendorEntity vendor = VendorEntity.builder()
                .name(request.getName())
                .bank(bank)
                .build();

        VendorEntity savedVendor = vendorRepository.save(vendor);
        return mapper.toDTO(savedVendor);
    }

    // =========================
    // BANK ADMIN: CREATE VENDOR ADMIN
    // =========================
    @Transactional
    public UserResponse createVendorAdmin(CreateVendorAdminRequest request) {
        // Get vendor by ID (not by bankId + vendorId)
        VendorEntity vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        RoleEntity role = roleRepository.findByName("ROLE_VENDOR_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_VENDOR_ADMIN missing"));

        // Get bank from vendor
        BankEntity bank = vendor.getBank();

        UserEntity admin = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .location(request.getLocation())
                .organization(request.getOrganization())
                .bank(bank)
                .vendor(vendor)
                .roles(Set.of(role))
                .enabled(true)
                .build();

        UserEntity savedAdmin = userRepository.save(admin);
        return mapper.toDTO(savedAdmin);
    }

    // =========================
    // VENDOR ADMIN: CREATE FIELD OFFICER
    // =========================
    // =========================
// CREATE FIELD OFFICER
// =========================
    @Transactional
    public UserResponse createFO(CreateFORequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }

        BankEntity bank = bankRepository.findById(request.getBankId())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        VendorEntity vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        RoleEntity role = roleRepository.findByName("ROLE_FO")
                .orElseThrow(() -> new IllegalStateException("ROLE_FO missing"));

        UserEntity fo = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .location(request.getLocation())
                .organization(request.getOrganization())
                .bank(bank)
                .vendor(vendor)
                .roles(Set.of(role))
                .enabled(true)
                .build();

        return mapper.toDTO(userRepository.save(fo));
    }

}