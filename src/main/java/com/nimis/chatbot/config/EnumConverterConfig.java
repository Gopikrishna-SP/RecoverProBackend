package com.nimis.chatbot.config;

import com.nimis.chatbot.model.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Custom Enum Converter Configuration
 * Converts String form parameters to Enum types automatically
 * E.g., "PAID" → Disp.PAID
 */
@Slf4j
@Component
public class EnumConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        log.info("Registering Enum converters");

        // Disp Enum Converter
        registry.addConverter(String.class, Disp.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                Disp disp = Disp.valueOf(source);
                log.debug("Converted '{}' to Disp.{}", source, disp);
                return disp;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Disp value: {}", source);
                return null;
            }
        });

        // Contactability Enum Converter
        registry.addConverter(String.class, Contactability.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                Contactability contactability = Contactability.valueOf(source);
                log.debug("Converted '{}' to Contactability.{}", source, contactability);
                return contactability;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Contactability value: {}", source);
                return null;
            }
        });

        // ResidenceStatus Enum Converter
        registry.addConverter(String.class, ResidenceStatus.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                ResidenceStatus residenceStatus = ResidenceStatus.valueOf(source);
                log.debug("Converted '{}' to ResidenceStatus.{}", source, residenceStatus);
                return residenceStatus;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid ResidenceStatus value: {}", source);
                return null;
            }
        });

        // ClassificationCode Enum Converter
        registry.addConverter(String.class, ClassificationCode.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                ClassificationCode classificationCode = ClassificationCode.valueOf(source);
                log.debug("Converted '{}' to ClassificationCode.{}", source, classificationCode);
                return classificationCode;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid ClassificationCode value: {}", source);
                return null;
            }
        });

        // OfficeStatus Enum Converter
        registry.addConverter(String.class, OfficeStatus.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                OfficeStatus officeStatus = OfficeStatus.valueOf(source);
                log.debug("Converted '{}' to OfficeStatus.{}", source, officeStatus);
                return officeStatus;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid OfficeStatus value: {}", source);
                return null;
            }
        });

        // ReasonForDefault Enum Converter
        registry.addConverter(String.class, ReasonForDefault.class, source -> {
            if (source == null || source.isEmpty() || "undefined".equals(source)) {
                return null;
            }
            try {
                ReasonForDefault reasonForDefault = ReasonForDefault.valueOf(source);
                log.debug("Converted '{}' to ReasonForDefault.{}", source, reasonForDefault);
                return reasonForDefault;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid ReasonForDefault value: {}", source);
                return null;
            }
        });

        log.info("Enum converters registered successfully");
    }
}