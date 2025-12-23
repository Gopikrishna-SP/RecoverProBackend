package com.nimis.chatbot.util;

public class ExcelHeaderUtil {

    public static String normalize(String header) {
        if (header == null) return "";
        return header
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "")
                .trim();
    }
}
