package com.study.studypal.user.service;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
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
