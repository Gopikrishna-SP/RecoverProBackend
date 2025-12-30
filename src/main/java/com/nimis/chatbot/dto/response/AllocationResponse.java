package com.nimis.chatbot.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AllocationResponse {

    private String loanNumber;
    private String segment;
    private String product;
    private String zone;
    private String state;
    private String branch;
    private String location;

    private String customerName;

    private BigDecimal disbursedAmountCr;
    private LocalDate disbursedDate;

    private BigDecimal posCr;
    private BigDecimal posAmt;
    private BigDecimal emi;

    private LocalDate emiStartDate;
    private LocalDate emiEndDate;

    private String bktTag;
    private String openingBkt;
    private String ashvDaPtc;
    private String securitization;
    private String seInse;
}
