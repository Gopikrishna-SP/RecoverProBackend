package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.response.SuperAdminDashboardResponse;
import com.nimis.chatbot.repository.BankRepository;
import com.nimis.chatbot.repository.VendorRepository;
import com.nimis.chatbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final VendorRepository vendorRepository;
    private final BankRepository bankRepository;
    private final UserRepository userRepository;

    public SuperAdminDashboardResponse getDashboardStats() {
        SuperAdminDashboardResponse stats = new SuperAdminDashboardResponse();

        stats.setTotalBanks(bankRepository.count());
        stats.setTotalVendors(vendorRepository.count());

        // Get counts by user role using role names from role table
        long bankAdminCount = userRepository.countByRoleName("ROLE_BANK_ADMIN");
        long vendorAdminCount = userRepository.countByRoleName("ROLE_VENDOR_ADMIN");
        long fieldExecutiveCount = userRepository.countByRoleName("ROLE_FO");

        stats.setTotalBankAdmin(bankAdminCount);
        stats.setTotalVendorAdmin(vendorAdminCount);
        stats.setTotalFieldExecutives(fieldExecutiveCount);

        stats.setActiveVendors(vendorRepository.findAll()
                .stream()
                .map(v -> v.getName())
                .limit(4)
                .collect(Collectors.toList()));

        stats.setActiveBanks(bankRepository.findAll()
                .stream()
                .map(b -> b.getName())
                .limit(4)
                .collect(Collectors.toList()));

        return stats;
    }
}