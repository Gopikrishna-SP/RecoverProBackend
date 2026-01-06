package com.nimis.chatbot.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldExecutiveCaseResponse {

    private String segment;
    private String location;
    private String loanNumber;
    private String customerName;
    private Double posInCr;
    private Double posAmount;
    private Integer emi;
    private Integer emiOverdue;
    private String mobile;
    private Integer emiDueCount;
    private String bktTag;
    private String openingBucket;
    private String securitization;
    private String ashvDaPtc;
    private String warrant;
    private String coApplicant1Name;
    private String coApplicant1Mobile;
    private String addressPriority1;
    private String addressPriority2;
    private String addressPriority3;
    private String addressPriority4;
}
