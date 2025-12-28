package com.study.studypal.file.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.file.dto.folder.request.CreateFolderRequestDto;
import com.study.studypal.file.dto.folder.request.UpdateFolderRequestDto;
import com.study.studypal.file.dto.folder.response.FolderDetailResponseDto;
import com.study.studypal.file.dto.folder.response.FolderResponseDto;
import com.study.studypal.file.dto.folder.response.ListFolderResponseDto;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.FolderErrorCode;
import com.study.studypal.file.repository.FolderRepository;
import com.study.studypal.file.service.api.FolderService;
import com.study.studypal.file.service.internal.FolderValidationService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
  private final FolderRepository folderRepository;
  private final TeamMembershipInternalService memberService;
  private final FolderValidationService validationService;
  private final ModelMapper modelMapper;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public FolderResponseDto createFolder(UUID userId, UUID teamId, CreateFolderRequestDto request) {
    validationService.validateCreateFolderPermission(userId, teamId);
    validationService.validateFolderName(userId, teamId, request.getName());

    User user = entityManager.getReference(User.class, userId);
    Team team = teamId != null ? entityManager.getReference(Team.class, teamId) : null;
    LocalDateTime now = LocalDateTime.now();

    Folder folder =
        Folder.builder()
            .name(request.getName())
            .createdBy(user)
            .createdAt(now)
            .updatedBy(user)
            .updatedAt(now)
            .bytes(0L)
            .documentCount(0)
            .isDeleted(false)
            .team(team)
            .build();

    folderRepository.save(folder);
    return modelMapper.map(folder, FolderResponseDto.class);
  }

  @Override
  public FolderDetailResponseDto getFolderDetail(UUID userId, UUID folderId) {
    Folder folder =
        folderRepository
            .findById(folderId)
            .orElseThrow(() -> new BaseException(FolderErrorCode.FOLDER_NOT_FOUND));

    validationService.validateViewFolderPermission(userId, folder);

    FolderDetailResponseDto responseDto = modelMapper.map(folder, FolderDetailResponseDto.class);
    responseDto.setCreatedBy(folder.getCreatedBy().getName());
    responseDto.setUpdatedBy(folder.getUpdatedBy().getName());

    return responseDto;
  }

  @Override
  public ListFolderResponseDto getFolders(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    if (teamId != null) {
      memberService.validateUserBelongsToTeam(userId, teamId);
    }

    List<Folder> folders =
        teamId != null
            ? getTeamDeletedTasks(teamId, cursor, pageable)
            : getPersonalDeletedTasks(userId, cursor, pageable);

    List<FolderResponseDto> foldersDTO =
        modelMapper.map(folders, new TypeToken<List<FolderResponseDto>>() {}.getType());

    long total =
        teamId != null
            ? folderRepository.countTeamFolders(teamId)
            : folderRepository.countPersonalFolders(userId);

    LocalDateTime nextCursor =
        folders.size() == size ? folders.get(folders.size() - 1).getUpdatedAt() : null;

    return ListFolderResponseDto.builder()
        .folders(foldersDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public ActionResponseDto updateFolderName(
      UUID userId, UUID folderId, UpdateFolderRequestDto request) {
    Folder folder =
        folderRepository
            .findByIdForUpdate(folderId)
            .orElseThrow(() -> new BaseException(FolderErrorCode.FOLDER_NOT_FOUND));

    validationService.validateUpdateFolderPermission(userId, folder);

    String name = request.getName();
    if (StringUtils.isNotBlank(name) && !name.equals(folder.getName())) {
      UUID teamId = folder.getTeam() != null ? folder.getTeam().getId() : null;
      validationService.validateFolderName(userId, teamId, name);

      folder.setName(name);
      trackingUpdate(userId, folder);

      folderRepository.save(folder);
    }

    return ActionResponseDto.builder().success(true).message("Update successfully.").build();
  }

  @Override
  public ActionResponseDto deleteFolder(UUID userId, UUID folderId) {
    Folder folder =
        folderRepository
            .findByIdForUpdate(folderId)
            .orElseThrow(() -> new BaseException(FolderErrorCode.FOLDER_NOT_FOUND));

    validationService.validateUpdateFolderPermission(userId, folder);

    folder.setIsDeleted(true);
    trackingUpdate(userId, folder);

    folderRepository.save(folder);

    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  private List<Folder> getPersonalDeletedTasks(
      UUID userId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? folderRepository.getPersonalFolders(userId, pageable)
        : folderRepository.getPersonalFoldersWithCursor(userId, cursor, pageable);
  }

  private List<Folder> getTeamDeletedTasks(UUID teamId, LocalDateTime cursor, Pageable pageable) {
    return cursor == null
        ? folderRepository.getTeamFolders(teamId, pageable)
        : folderRepository.getTeamFoldersWithCursor(teamId, cursor, pageable);
  }

  private void trackingUpdate(UUID userId, Folder folder) {
    User user = entityManager.getReference(User.class, userId);
    folder.setUpdatedBy(user);
    folder.setUpdatedAt(LocalDateTime.now());
  }
}
