package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.User.request.UpdateUserRequestDto;
import com.study.studypal.dtos.User.response.ListUserResponseDto;
import com.study.studypal.dtos.User.response.UserDetailResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    UUID createDefaultProfile(String name);
    UUID createProfile(String name, String avatarUrl);
    UserDetailResponseDto getUserById(UUID id);
    ListUserResponseDto searchUsersByName(UUID userId, String keyword, UUID cursor, int size);
    UserDetailResponseDto updateUser(UUID id, UpdateUserRequestDto request);
    ActionResponseDto uploadUserAvatar(UUID id, MultipartFile file);
}
