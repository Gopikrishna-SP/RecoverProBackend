package com.nimis.chatbot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "upload_error")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rowNumber;
    private String columnName;
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "upload_file_id")
    private UploadFile uploadFile;
}
