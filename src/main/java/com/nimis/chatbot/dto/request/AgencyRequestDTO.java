package com.nimis.chatbot.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgencyRequestDTO {

    private String agencyCode;
    private String agencyName;

    private String managerEmpId;
    private String managerName;

    private String zonalManagerEmpId;
    private String zonalManagerName;
}
