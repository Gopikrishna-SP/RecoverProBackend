package com.nimis.chatbot.utility;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {"csv", "xlsx", "xls"};

    public static void validateFile(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }

        String extension = getFileExtension(filename).toLowerCase();
        boolean isAllowed = false;
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed. Only CSV, XLSX, XLS are supported");
        }
    }

    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot + 1) : "";
    }
}