package com.nimis.chatbot.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {

    private String mainApplicantName;
    private String mainApplicantMobileNo;
    private String panMainApplicant;
    private LocalDate dobMainApplicant;

    private String coApplicantName;
    private String coApplicantMobileNo;
    private String panCoApplicant;
    private LocalDate dobCoApplicant;

    private String relationWithMainApplicant;
}
