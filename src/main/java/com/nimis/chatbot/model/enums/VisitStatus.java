package com.nimis.chatbot.model.enums;

public enum VisitStatus {

    VERIFIED,                 // GPS within allowed radius
    NOT_VERIFIED,             // GPS outside allowed radius
    GPS_MISMATCH,             // Large distance from target location
    MOCK_LOCATION_DETECTED,   // Fake GPS / mock provider detected
    GPS_UNAVAILABLE           // GPS not available or disabled
}
