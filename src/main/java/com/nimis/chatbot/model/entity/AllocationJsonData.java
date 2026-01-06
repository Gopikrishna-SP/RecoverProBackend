package com.nimis.chatbot.model.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationJsonData {

    private String segment;
    private String product;
    private String state;
    private String branch;
    private String location;
    private String customerName;

    private BigDecimal posInCr;
    private BigDecimal emi;
    private Integer bkt;
}
