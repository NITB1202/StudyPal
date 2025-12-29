package com.study.studypal.user.service.internal.impl;

import com.study.studypal.chatbot.service.internal.UserQuotaService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.service.internal.UsageService;
import com.study.studypal.plan.service.internal.TaskCounterService;
import com.study.studypal.user.dto.internal.UserSummaryProfile;
import com.study.studypal.user.entity.User;
import com.study.studypal.user.enums.Gender;
import com.study.studypal.user.exception.UserErrorCode;
import com.study.studypal.user.repository.UserRepository;
import com.study.studypal.user.service.internal.UserInternalService;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserInternalServiceImpl implements UserInternalService {
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final TaskCounterService taskCounterService;
  private final UserQuotaService quotaUsageService;
  private final UsageService usageService;

  @Override
  public UUID createDefaultProfile(String name) {
    User user = User.builder().name(name).gender(Gender.UNSPECIFIED).build();
    UUID userId = userRepository.save(user).getId();
    taskCounterService.createUserTaskCounter(userId);
    quotaUsageService.initializeUsage(userId);
    usageService.createUserUsage(userId);
    return userId;
  }

  @Override
  public UUID createProfile(String name, String avatarUrl) {
    User user = User.builder().name(name).gender(Gender.UNSPECIFIED).avatarUrl(avatarUrl).build();
    UUID userId = userRepository.save(user).getId();
    taskCounterService.createUserTaskCounter(userId);
    quotaUsageService.initializeUsage(userId);
    usageService.createUserUsage(userId);
    return userId;
  }

  @Override
  public UserSummaryProfile getUserSummaryProfile(UUID userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));

    return modelMapper.map(user, UserSummaryProfile.class);
  }
}
