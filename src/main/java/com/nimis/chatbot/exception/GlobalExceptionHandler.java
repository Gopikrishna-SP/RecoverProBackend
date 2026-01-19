package com.nimis.chatbot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", e.getMessage() != null ? e.getMessage() : "Internal server error");
        response.put("type", e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("File upload size exceeded", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", "File size exceeds maximum limit (10MB)");
        response.put("type", "FILE_SIZE_EXCEEDED");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", true);
        response.put("message", e.getMessage());
        response.put("type", "INVALID_ARGUMENT");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException e) {
        log.warn("Data integrity violation", e);

        Map<String, Object> response = new HashMap<>();
        response.put("error", true);

        // Check for unique constraint violation
        if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
            response.put("message", "Duplicate loan number found. Each loan number must be unique.");
        } else {
            response.put("message", "Database constraint violation: " + e.getMostSpecificCause().getMessage());
        }
        response.put("type", "DATA_INTEGRITY_VIOLATION");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}