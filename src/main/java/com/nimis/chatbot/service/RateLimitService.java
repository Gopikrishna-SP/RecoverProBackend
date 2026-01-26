package com.nimis.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RateLimitService {

    private final Map<String, RateLimitEntry> store = new HashMap<>();

    public boolean allowRequest(String key, int maxAttempts, Duration duration) {
        RateLimitEntry entry = store.get(key);
        Instant now = Instant.now();

        if (entry == null) {
            store.put(key, new RateLimitEntry(1, now.plus(duration)));
            return true;
        }

        if (now.isAfter(entry.expiresAt)) {
            store.put(key, new RateLimitEntry(1, now.plus(duration)));
            return true;
        }

        if (entry.attempts >= maxAttempts) {
            return false;
        }

        entry.attempts++;
        return true;
    }

    private static class RateLimitEntry {
        int attempts;
        Instant expiresAt;

        RateLimitEntry(int attempts, Instant expiresAt) {
            this.attempts = attempts;
            this.expiresAt = expiresAt;
        }
    }
}
