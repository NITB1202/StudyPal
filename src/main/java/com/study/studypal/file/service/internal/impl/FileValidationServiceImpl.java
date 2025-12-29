package com.study.studypal.file.service.internal.impl;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.FileUtils;
import com.study.studypal.file.config.FileProperties;
import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.exception.UserFileErrorCode;
import com.study.studypal.file.service.internal.FileValidationService;
import com.study.studypal.file.service.internal.FolderInternalService;
import com.study.studypal.file.service.internal.FolderValidationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileValidationServiceImpl implements FileValidationService {
  private final FolderInternalService folderService;
  private final FolderValidationService folderValidationService;
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
  public void validateViewFolderPermission(UUID userId, UUID folderId) {
    Folder folder = folderService.getById(folderId);
    folderValidationService.validateViewFolderPermission(userId, folder);
  }

  @Override
  public void validateViewFolderPermission(UUID userId, Folder folder) {
    folderValidationService.validateViewFolderPermission(userId, folder);
  }
}
