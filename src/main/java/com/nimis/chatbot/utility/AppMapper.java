package com.nimis.chatbot.utility;

import com.nimis.chatbot.dto.response.*;
import com.nimis.chatbot.model.entity.BankEntity;
import com.nimis.chatbot.model.entity.RoleEntity;
import com.nimis.chatbot.model.entity.UserEntity;
import com.nimis.chatbot.model.entity.VendorEntity;
import org.springframework.stereotype.Component;

@Component
public class AppMapper {

    public UserResponse toDTO(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getFullName())
                .email(user.getEmail())
                .role(user.getRoles().stream().findFirst().map(RoleEntity::getName).orElse(""))
                .build();
    }

    public VendorResponse toDTO(VendorEntity vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .bankId(vendor.getBank().getId())
                .build();
    }

    public BankResponse toDTO(BankEntity bank) {
        return BankResponse.builder()
                .id(bank.getId())
                .name(bank.getName())
                .build();
    }
}
