package com.nimis.chatbot.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CustomerResponse {

    private String mainApplicantName;
    private String mainApplicantMobile;
    private String panMainApp;
    private LocalDate dobMainApp;

    private String coApplicantName;
    private String coApplicantMobile;
    private String panCoApp;
    private LocalDate dobCoApp;

    private String relationWithMainApplicant;
}
