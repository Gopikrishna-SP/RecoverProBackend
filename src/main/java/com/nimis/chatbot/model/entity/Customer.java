package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

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
