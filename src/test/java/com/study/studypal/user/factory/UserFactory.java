package com.study.studypal.user.factory;

import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import com.study.studypal.user.entity.User;

import java.util.UUID;

public class UserFactory {

    // ----------------- Entity -----------------

    public static User createWithId() {
        return User.builder()
                .id(UUID.randomUUID())
                .name("user_" + (int)(Math.random() * 10000))
                .build();
    }

    public static User createWithId(String name) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(name)
                .build();
    }

    public static User createForSave() {
        return User.builder()
                .name("user_" + (int)(Math.random() * 10000))
                .build();
    }

    public static User createForSave(String name) {
        return User.builder()
                .name(name)
                .build();
    }

    // ----------------- DTO -----------------

    public static UserSummaryResponseDto createUserSummaryResponseDto(UUID id, String name) {
        return UserSummaryResponseDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static UserDetailResponseDto createUserDetailResponseDto(UUID id, String name) {
        return UserDetailResponseDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static UpdateUserRequestDto createUpdateUserRequestDto(String newName) {
        return UpdateUserRequestDto.builder()
                .name(newName)
                .build();
    }
}
