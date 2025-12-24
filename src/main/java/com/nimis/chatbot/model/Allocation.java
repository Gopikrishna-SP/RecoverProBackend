package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number")
    private String loanNumber;

    @Column(name = "customer_name")
    private String customerName;

    private String segment;
    private String product;
    private String zone;
    private String state;
    private String branch;
    private String location;

    private Double pos;
    private Double emi;

    @Column(name = "bkt_tag")
    private String bktTag;
}
