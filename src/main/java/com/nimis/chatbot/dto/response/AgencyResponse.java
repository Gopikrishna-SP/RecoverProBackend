package com.nimis.chatbot.dto.response;

import lombok.Data;

@Data
public class AgencyResponse {

    private String agencyCode;
    private String agencyName;

    private String managerEmpId;
    private String managerName;

    private String zmEmpId;
    private String zonalManager;
}
