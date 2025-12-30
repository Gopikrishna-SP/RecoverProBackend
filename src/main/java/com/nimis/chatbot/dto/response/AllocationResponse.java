package com.nimis.chatbot.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class AllocationResponse {
    private String loanNumber;
    private Map<String, Object> allocationData;
}
