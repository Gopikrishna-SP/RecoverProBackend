package com.nimis.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        // Set timezone BEFORE Spring starts
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("========================================");
        System.out.println("TIMEZONE FORCED TO: " + TimeZone.getDefault().getID());
        System.out.println("========================================");

        SpringApplication.run(ChatbotApplication.class, args);
    }
}