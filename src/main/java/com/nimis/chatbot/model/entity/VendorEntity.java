package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendors", uniqueConstraints = @UniqueConstraint(columnNames = {"bank_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private BankEntity bank;
}