package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    // Address priority fields
    private String addressPriority1;
    private String addressPriority2;
    private String addressPriority3;
    private String addressPriority4;
    private String addressPriority5;
    private String addressPriority6;
    private String addressPriority7;
    private String addressPriority8;

    private String address_1;
    private String address_2;
    private String address_3;
    private String address_4;
    private String address_5;
    private String address_6;
    private String address_7;
    private String address_8;
    private String address_9;
    private String address_10;



    // Other address-related fields
    private String businessPinCode;
    private String residencePinCode;
    private String mainPinCode;
}
