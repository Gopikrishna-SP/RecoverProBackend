package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agency_details")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "allocation_id")
    private Allocation allocation;

    private String agencyCode;
    private String agencyName;

    private String managerEmpId;
    private String managerName;

    private String zmEmpId;
    private String zonalManager;
}
