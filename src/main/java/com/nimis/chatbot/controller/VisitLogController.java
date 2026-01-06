package com.nimis.chatbot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimis.chatbot.dto.request.VisitLogRequestDTO;
import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.service.VisitLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/visit-logs")
@RequiredArgsConstructor
public class VisitLogController {

    private final VisitLogService visitLogService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisitLogResponseDTO> createVisitLog(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,

            @RequestHeader("X-Latitude") String latitude,
            @RequestHeader("X-Longitude") String longitude,
            @RequestHeader(value = "X-Location-Accuracy", required = false) String accuracy,
            @RequestHeader(value = "X-Geo-Address", required = false) String geoAddress,

            Principal principal
    ) throws Exception {

        VisitLogRequestDTO request =
                objectMapper.readValue(data, VisitLogRequestDTO.class);

        Authentication authentication = (Authentication) principal;
        UserEntity user = (UserEntity) authentication.getPrincipal();

        return ResponseEntity.ok(
                visitLogService.create(
                        request,
                        image,
                        parseDouble(latitude, "Latitude"),
                        parseDouble(longitude, "Longitude"),
                        parseNullableDouble(accuracy),
                        geoAddress,
                        user.getFullName() // FULL NAME
                )
        );
    }

    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<List<VisitLogResponseDTO>> getByAllocation(
            @PathVariable Long allocationId
    ) {
        return ResponseEntity.ok(
                visitLogService.getByAllocationId(allocationId)
        );
    }

    private Double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number");
        }
    }

    private Double parseNullableDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Location Accuracy must be a valid number");
        }
    }
}
