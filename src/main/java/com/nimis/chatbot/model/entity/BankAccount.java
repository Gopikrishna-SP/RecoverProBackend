package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    // Account 1
    private String accountHolder1;
    private String bankName1;
    private String accountNumber1;
    private String ifscCode1;

    // Account 2
    private String accountHolder2;
    private String bankName2;
    private String accountNumber2;
    private String ifscCode2;

    // Account 3
    private String accountHolder3;
    private String bankName3;
    private String accountNumber3;
    private String ifscCode3;
}
