package com.nimis.chatbot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldExecutiveDashboardResponse {
    private long totalCases;
    private long completedVisits;
    private long pendingCases;
    private long inProgress;
}