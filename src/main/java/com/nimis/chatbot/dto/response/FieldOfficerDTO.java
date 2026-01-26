package com.nimis.chatbot.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldOfficerDTO {
    private Long id;
    private String name;
    private String email;
    private String status; // active, inactive
    private Integer totalCases;
    private Integer completedCases;
    private Integer pendingCases;
    private Integer cases; // Legacy field for compatibility
    private Double completionRate;
    private BigDecimal collectionsAmount;
    private String date;

    // Alias for cases count (for compatibility)
    public Integer getCases() {
        return cases != null ? cases : totalCases;
    }
}