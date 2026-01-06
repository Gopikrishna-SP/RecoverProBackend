package com.nimis.chatbot.service;

import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.repository.AllocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldExecutiveServiceTest {

    @Mock
    AllocationRepository allocationRepository;

    @Mock
    VisitLogRepository visitLogRepository;

    @InjectMocks
    FieldExecutiveService service;

    @Captor
    ArgumentCaptor<Allocation> allocationCaptor;

    @Captor
    ArgumentCaptor<VisitLog> visitLogCaptor;

    Allocation existing;

    @BeforeEach
    void setUp() {
        existing = new Allocation();
        existing.setLoanNumber("LN123");
        existing.setFieldExecutiveId(10L);
        existing.setVisitCount(null);
        existing.setStatus("ASSIGNED");
    }

    @Test
    void getMyCases_nullUser_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getMyCases(null));
    }

    @Test
    void updateVisit_invalidStatus_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                service.updateVisit(10L, "LN123", "BAD_STATUS", null, null)
        );
    }

    @Test
    void updateVisit_unauthorized_throws() {
        when(allocationRepository.findByLoanNumber("LN123")).thenReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class, () ->
                service.updateVisit(99L, "LN123", "VISITED", null, null)
        );
    }

    @Test
    void updateVisit_paymentRequiresAmount_throws() {
        when(allocationRepository.findByLoanNumber("LN123")).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                service.updateVisit(10L, "LN123", "PAYMENT_COLLECTED", null, null)
        );
    }

    @Test
    void updateVisit_success_updatesAndLogs() {
        when(allocationRepository.findByLoanNumber("LN123")).thenReturn(Optional.of(existing));

        service.updateVisit(10L, "LN123", "VISITED", null, "ok");

        verify(allocationRepository).save(allocationCaptor.capture());
        Allocation saved = allocationCaptor.getValue();
        assertEquals("VISITED", saved.getStatus());
        assertNotNull(saved.getLastVisitedAt());
        assertEquals(1, saved.getVisitCount());

        verify(visitLogRepository).save(visitLogCaptor.capture());
        VisitLog log = visitLogCaptor.getValue();
        assertEquals("LN123", log.getLoanNumber());
        assertEquals(10L, log.getUserId());
        assertEquals("VISITED", log.getStatus());
        assertEquals("ok", log.getRemarks());
    }
}