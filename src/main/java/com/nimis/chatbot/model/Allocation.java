package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
}
