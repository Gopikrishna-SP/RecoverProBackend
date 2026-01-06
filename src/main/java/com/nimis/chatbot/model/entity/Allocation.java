package com.nimis.chatbot.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "loan_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false, unique = true)
    private String loanNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "allocation_data", columnDefinition = "jsonb")
    private Map<String, Object> allocationData;

    private Long fieldExecutiveId;

    @Column(length = 30)
    private String status;

    private LocalDateTime assignedAt;
    private LocalDateTime lastVisitedAt;

    @Column(nullable = false)
    private Integer visitCount = 0;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (status == null) status = "UNASSIGNED";
        if (visitCount == null) visitCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
