package com.study.studypal.file.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.entity.TeamUsage;
import com.study.studypal.file.entity.UserUsage;
import com.study.studypal.file.exception.UsageErrorCode;
import com.study.studypal.file.repository.TeamUsageRepository;
import com.study.studypal.file.repository.UserUsageRepository;
import com.study.studypal.file.service.internal.UsageService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
  private final UserUsageRepository userUsageRepository;
  private final TeamUsageRepository teamUsageRepository;

  @Override
  public UserUsage getUserUsage(UUID userId) {
    return userUsageRepository
        .findById(userId)
        .orElseThrow(() -> new BaseException(UsageErrorCode.USAGE_NOT_FOUND));
  }

  @Override
  public TeamUsage getTeamUsage(UUID teamId) {
    return teamUsageRepository
        .findById(teamId)
        .orElseThrow(() -> new BaseException(UsageErrorCode.USAGE_NOT_FOUND));
  }
}
