package com.nimis.chatbot.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AllocationRequestDTO {

    private String loanNumber;

    private String segment;
    private String product;
    private String zone;
    private String state;
    private String branch;
    private String location;

    private BigDecimal disbursedAmountCr;
    private LocalDate disbursedDate;

    private BigDecimal posCr;
    private BigDecimal posAmount;

    private BigDecimal emi;
    private LocalDate emiStartDate;
    private LocalDate emiEndDate;

    private String bktTag;
    private String openingBkt;

    private String ashvDaPtc;
    private String securitization;
    private String seInse;

    private String monthLastNotice;
    private LocalDate noticeDate;


    private CustomerRequestDTO customer;
    private AgencyRequestDTO agency;
    private List<AddressRequestDTO> addresses;
    private List<PhoneRequestDTO> phones;
    private List<BankAccountRequestDTO> bankAccounts;
    private List<LegalCaseRequestDTO> legalCases;
}
