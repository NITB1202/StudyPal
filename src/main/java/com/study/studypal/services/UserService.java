package com.study.studypal.services;

import com.study.studypal.dtos.Shared.ActionResponseDto;
import com.study.studypal.dtos.User.request.UpdateUserRequestDto;
import com.study.studypal.dtos.User.response.ListUserResponseDto;
import com.study.studypal.dtos.User.response.UserDetailResponseDto;
import com.study.studypal.dtos.User.response.UserSummaryResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    UUID createDefaultProfile(String name);
    UUID createProfile(String name, String avatarUrl);
    UserSummaryResponseDto getUserSummaryProfile(UUID userId);
    UserDetailResponseDto getUserProfile(UUID userId);
    ListUserResponseDto searchUsersByName(UUID userId, String keyword, UUID cursor, int size);
    UserDetailResponseDto updateUser(UUID userId, UpdateUserRequestDto request);
    ActionResponseDto uploadUserAvatar(UUID userId, MultipartFile file);
}
