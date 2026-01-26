package com.nimis.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditService {

    public void log(String action, String email, String ipAddress) {
        log.info("AUDIT - Action: {}, Email: {}, IP: {}", action, email, ipAddress);
        // Later: save to database audit table
    }
}
