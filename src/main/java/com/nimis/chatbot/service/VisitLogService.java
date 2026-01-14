package com.nimis.chatbot.service;

import com.nimis.chatbot.dto.request.VisitLogRequestDTO;
import com.nimis.chatbot.dto.response.VisitLogResponseDTO;
import com.nimis.chatbot.model.entity.Allocation;
import com.nimis.chatbot.model.entity.VisitLog;
import com.nimis.chatbot.repository.VisitLogRepository;
import com.nimis.chatbot.util.VisitLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class VisitLogService {

    private final VisitLogRepository visitLogRepository;
    private final AllocationUploadService allocationService;

    public VisitLogResponseDTO create(
            VisitLogRequestDTO request,
            MultipartFile image,
            Double latitude,
            Double longitude,
            Double locationAccuracy,
            String geoAddress,
            String createdBy
    ) {

        LocalDate visitDate = request.getVisitDate() != null
                ? request.getVisitDate()
                : LocalDate.now();

        if (visitLogRepository.existsByAllocationIdAndVisitDateAndCreatedBy(
                request.getAllocationId(), visitDate, createdBy)) {
            throw new RuntimeException("Visit already logged for today");
        }

        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            try {
                String baseDir = System.getProperty("user.dir") + "/uploads/visit/"
                        + request.getAllocationId();

                Files.createDirectories(Paths.get(baseDir));

                String fileName = UUID.randomUUID() + ".jpg";
                Path fullPath = Paths.get(baseDir, fileName);

                Files.write(fullPath, image.getBytes());

                imagePath = "/uploads/visit/"
                        + request.getAllocationId()
                        + "/"
                        + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to store visit image", e);
            }
        }


        VisitLog visitLog = VisitLog.builder()
                .allocationId(request.getAllocationId())
                .visitDate(visitDate)
                .createdBy(createdBy)

                .disp(request.getDisp())
                .projection(request.getProjection())
                .amount(request.getAmount())
                .ptpDate(request.getPtpDate())
                .reasonForDefault(request.getReasonForDefault())
                .contactability(request.getContactability())
                .residenceStatus(request.getResidenceStatus())
                .officeStatus(request.getOfficeStatus())
                .classificationCode(request.getClassificationCode())
                .customerProfile(request.getCustomerProfile())
                .fieldUpdateFeedback(request.getFieldUpdateFeedback())

                .latitude(latitude)
                .longitude(longitude)
                .locationAccuracy(locationAccuracy)
                .geoAddress(geoAddress)
                .visitImagePath(imagePath)
                .build();

        VisitLog saved = visitLogRepository.save(visitLog);

        Allocation allocation = allocationService.getById(saved.getAllocationId());

        return VisitLogMapper.toResponse(saved, allocation);
    }

    public List<VisitLogResponseDTO> getByAllocationId(Long allocationId) {

        Allocation allocation = allocationService.getById(allocationId);

        return visitLogRepository.findByAllocationId(allocationId)
                .stream()
                .map(v -> VisitLogMapper.toResponse(v, allocation))
                .collect(Collectors.toList());
    }

    public List<VisitLogResponseDTO> getAll() {

        return visitLogRepository.findAll()
                .stream()
                .map(visitLog -> {
                    Allocation allocation =
                            allocationService.getById(visitLog.getAllocationId());
                    return VisitLogMapper.toResponse(visitLog, allocation);
                })
                .collect(Collectors.toList());
    }

}
