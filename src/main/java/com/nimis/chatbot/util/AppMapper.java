package com.nimis.chatbot.util;

import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.model.*;
import org.springframework.stereotype.Component;

@Component
public class AppMapper {

    public UserResponseDTO toDTO(UserEntity user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .role(user.getRoles().stream().findFirst().map(RoleEntity::getName).orElse(""))
                .build();
    }

    public VendorResponseDTO toDTO(VendorEntity vendor) {
        return VendorResponseDTO.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .bankId(vendor.getBank().getId())
                .build();
    }

    public BankResponseDTO toDTO(BankEntity bank) {
        return BankResponseDTO.builder()
                .id(bank.getId())
                .name(bank.getName())
                .build();
    }
}
