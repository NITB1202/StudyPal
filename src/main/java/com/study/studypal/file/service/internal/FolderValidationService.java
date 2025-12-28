package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.Folder;
import java.util.UUID;

public interface FolderValidationService {
  void validateCreateFolderPermission(UUID userId, UUID teamId);

  void validateFolderName(UUID userId, UUID teamId, String name);

  void validateViewFolderPermission(UUID userId, Folder folder);

  void validateUpdateFolderPermission(UUID userId, Folder folder);

  void validateFolderNotDeleted(Folder folder);
}
