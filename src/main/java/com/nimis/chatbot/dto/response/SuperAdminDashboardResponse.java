package com.nimis.chatbot.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class SuperAdminDashboardResponse {
    private long totalBanks;
    private long totalVendors;
    private long totalBankAdmin;
    private long totalVendorAdmin;
    private long totalFieldExecutives;
    private List<String> activeVendors;
    private List<String> activeBanks;
}