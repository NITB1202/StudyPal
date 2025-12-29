package com.study.studypal.file.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.config.FileProperties;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.entity.TeamUsage;
import com.study.studypal.file.entity.UserUsage;
import com.study.studypal.file.exception.UsageErrorCode;
import com.study.studypal.file.repository.TeamUsageRepository;
import com.study.studypal.file.repository.UserUsageRepository;
import com.study.studypal.file.service.internal.UsageService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {
  private final UserUsageRepository userUsageRepository;
  private final TeamUsageRepository teamUsageRepository;
  private final FileProperties fileProperties;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public void createUserUsage(UUID userId) {
    User user = entityManager.getReference(User.class, userId);

    UserUsage userUsage =
        UserUsage.builder()
            .user(user)
            .usageUsed(0L)
            .usageLimit(fileProperties.getUserUsageLimit())
            .build();

    userUsageRepository.save(userUsage);
  }

  @Override
  @Transactional
  public void createTeamUsage(UUID teamId) {
    Team team = entityManager.getReference(Team.class, teamId);

    TeamUsage teamUsage =
        TeamUsage.builder()
            .team(team)
            .usageUsed(0L)
            .usageLimit(fileProperties.getTeamUsageLimit())
            .build();

    teamUsageRepository.save(teamUsage);
  }

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

  @Override
  public void validateUsage(Folder folder, MultipartFile file) {
    if (folder.getTeam() != null) {
      validateTeamUsage(folder.getTeam().getId(), file);
    } else {
      validateUserUsage(folder.getCreatedBy().getId(), file);
    }
  }

  private void validateTeamUsage(UUID teamId, MultipartFile file) {
    TeamUsage teamUsage =
        teamUsageRepository
            .findById(teamId)
            .orElseThrow(() -> new BaseException(UsageErrorCode.USAGE_NOT_FOUND));

    if (teamUsage.getUsageUsed() + file.getSize() > teamUsage.getUsageLimit()) {
      throw new BaseException(UsageErrorCode.INSUFFICIENT_STORAGE_QUOTA);
    }
  }

  private void validateUserUsage(UUID userId, MultipartFile file) {
    UserUsage userUsage =
        userUsageRepository
            .findById(userId)
            .orElseThrow(() -> new BaseException(UsageErrorCode.USAGE_NOT_FOUND));

    if (userUsage.getUsageUsed() + file.getSize() > userUsage.getUsageLimit()) {
      throw new BaseException(UsageErrorCode.INSUFFICIENT_STORAGE_QUOTA);
    }
  }
}
