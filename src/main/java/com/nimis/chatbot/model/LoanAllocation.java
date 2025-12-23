package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanNumber;
    private String customerName;
    private String product;
    private String segment;

    private BigDecimal posAmount;
    private BigDecimal emi;

    private String bucket;
    private String branch;

    @ManyToOne
    @JoinColumn(name = "upload_file_id")
    private UploadFile sourceFile;
}
