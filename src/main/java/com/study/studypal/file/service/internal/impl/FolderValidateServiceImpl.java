package com.study.studypal.file.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.FolderErrorCode;
import com.study.studypal.file.repository.FolderRepository;
import com.study.studypal.file.service.internal.FolderValidationService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderValidateServiceImpl implements FolderValidationService {
  private final FolderRepository folderRepository;
  private final TeamMembershipInternalService memberService;

  @Override
  public void validateCreateFolderPermission(UUID userId, UUID teamId) {
    if (teamId != null) {
      memberService.validateUpdateFolderPermission(userId, teamId);
    }
  }

  @Override
  public void validateFolderName(UUID userId, UUID teamId, String name) {
    if (teamId != null) {
      validateTeamFolderName(teamId, name);
    } else {
      validatePersonalFolderName(userId, name);
    }
  }

  @Override
  public void validateViewFolderPermission(UUID userId, Folder folder) {
    Team team = folder.getTeam();
    if (team != null) {
      memberService.validateUserBelongsToTeam(userId, team.getId());
    } else {
      validateFolderOwnership(userId, folder);
    }
  }

  @Override
  public void validateUpdateFolderPermission(UUID userId, Folder folder) {
    Team team = folder.getTeam();
    if (team != null) {
      memberService.validateUpdateFolderPermission(userId, team.getId());
    } else {
      validateFolderOwnership(userId, folder);
    }
  }

  private void validateTeamFolderName(UUID teamId, String name) {
    if (folderRepository.existsByNameAndTeamId(name, teamId)) {
      throw new BaseException(FolderErrorCode.FOLDER_ALREADY_EXISTS);
    }
  }

  private void validatePersonalFolderName(UUID userId, String name) {
    if (folderRepository.existsByNameAndCreatedByAndTeamIdIsNull(name, userId)) {
      throw new BaseException(FolderErrorCode.FOLDER_ALREADY_EXISTS);
    }
  }

  private void validateFolderOwnership(UUID userId, Folder folder) {
    if (!folder.getCreatedBy().getId().equals(userId)) {
      throw new BaseException(FolderErrorCode.PERMISSION_FOLDER_OWNER_DENIED);
    }
  }
}
