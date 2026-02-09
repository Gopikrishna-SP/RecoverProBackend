package com.nimis.chatbot.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class TimezoneConfig implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Force UTC timezone before any database operations
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("=== TIMEZONE SET TO: " + TimeZone.getDefault().getID() + " ===");
    }
}