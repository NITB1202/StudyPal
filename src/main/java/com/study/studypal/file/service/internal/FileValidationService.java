package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.Folder;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {
  void validateFile(MultipartFile file);

  void validateViewFolderPermission(UUID userId, UUID folderId);

  void validateViewFolderPermission(UUID userId, Folder folder);
}
