package com.study.studypal.file.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.file.config.FileProperties;
import com.study.studypal.file.entity.File;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.UserFileErrorCode;
import com.study.studypal.file.repository.FileRepository;
import com.study.studypal.file.service.internal.FileValidationService;
import com.study.studypal.file.service.internal.FolderValidationService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileValidationServiceImpl implements FileValidationService {
  private final FileRepository fileRepository;
  private final FolderValidationService folderValidationService;
  private final TeamMembershipInternalService memberService;
  private final FileProperties properties;

  @Override
  public void validateFile(MultipartFile file) {
    if (!FileUtils.isDocument(file) && !FileUtils.isImage(file)) {
      throw new BaseException(UserFileErrorCode.UNSUPPORTED_FILE_TYPE);
    }

    if (file.getSize() > properties.getMaxSize().toBytes()) {
      throw new BaseException(UserFileErrorCode.FILE_SIZE_EXCEEDED);
    }
  }

  @Override
  public void validateViewFolderPermission(UUID userId, Folder folder) {
    folderValidationService.validateViewFolderPermission(userId, folder);
  }

  @Override
  public void validateFileName(UUID folderId, String name, String extension) {
    if (fileRepository.existsByFolderIdAndNameAndExtension(folderId, name, extension)) {
      throw new BaseException(UserFileErrorCode.FILE_ALREADY_EXISTS);
    }
  }

  @Override
  public void validateUpdateFilePermission(UUID userId, File file) {
    UUID creatorId = file.getCreatedBy().getId();
    if (creatorId.equals(userId)) return;

    Team team = file.getFolder().getTeam();
    if (team == null) {
      throw new BaseException(UserFileErrorCode.PERMISSION_FILE_OWNER_DENIED);
    }

    memberService.validateUpdateFolderPermission(userId, team.getId());
  }
}
