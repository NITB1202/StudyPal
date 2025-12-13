package com.study.studypal.chatbot.service.api.impl;

import com.study.studypal.chatbot.dto.response.UserQuotaUsageResponseDto;
import com.study.studypal.chatbot.entity.UserQuota;
import com.study.studypal.chatbot.exception.UserQuotaErrorCode;
import com.study.studypal.chatbot.repository.UserQuotaRepository;
import com.study.studypal.chatbot.service.api.UserQuotaUsageService;
import com.study.studypal.common.exception.BaseException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQuotaUsageServiceImpl implements UserQuotaUsageService {
  private final UserQuotaRepository userQuotaRepository;
  private final ModelMapper modelMapper;

  @Override
  public UserQuotaUsageResponseDto getUsage(UUID userId) {
    UserQuota userQuota =
        userQuotaRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(UserQuotaErrorCode.USER_QUOTA_NOT_FOUND));

    return modelMapper.map(userQuota, UserQuotaUsageResponseDto.class);
  }
}
