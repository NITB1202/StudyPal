package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.File;
import com.study.studypal.file.entity.Folder;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface FileValidationService {
  void validateFile(MultipartFile file);

  void validateFileName(UUID folderId, String name, String extension);

  void validateFileNotDeleted(File file);

  void validateFileDeleted(File file);

  void validateViewFolderPermission(UUID userId, Folder folder);

  void validateUpdateFilePermission(UUID userId, File file);
}
