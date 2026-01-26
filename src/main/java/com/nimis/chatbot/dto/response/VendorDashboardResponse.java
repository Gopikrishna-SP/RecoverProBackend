package com.nimis.chatbot.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDashboardResponse {
    private Long visitsPendingToday;
    private BigDecimal collectionsToday;
    private Long visitsCompletedToday;
    private Long totalActiveCases;
    private List<FieldOfficerDTO> topFieldOfficers;
    private List<AllocationDTO> recentAllocations;
}