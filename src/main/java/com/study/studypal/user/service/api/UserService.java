package com.study.studypal.user.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.user.dto.request.UpdateUserRequestDto;
import com.study.studypal.user.dto.response.ListUserResponseDto;
import com.study.studypal.user.dto.response.UserDetailResponseDto;
import com.study.studypal.user.dto.response.UserResponseDto;
import com.study.studypal.user.dto.response.UserSummaryResponseDto;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  UserSummaryResponseDto getUserSummaryProfile(UUID userId);

  UserDetailResponseDto getUserProfile(UUID userId);

  ListUserResponseDto searchUsersByNameOrEmail(UUID userId, String keyword, UUID cursor, int size);

  UserResponseDto updateUser(UUID userId, UpdateUserRequestDto request);

  ActionResponseDto uploadUserAvatar(UUID userId, MultipartFile file);
}
