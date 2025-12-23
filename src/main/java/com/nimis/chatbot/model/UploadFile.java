package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "upload_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String uploadedBy;
    private LocalDateTime uploadedAt;

    private Integer totalRows;
    private Integer successRows;
    private Integer failedRows;

    private String status; // PROCESSING, COMPLETED, FAILED
}
