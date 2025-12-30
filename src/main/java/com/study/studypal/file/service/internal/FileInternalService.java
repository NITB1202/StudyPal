package com.study.studypal.file.service.internal;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FileInternalService {
  void softDeleteFilesInFolder(UUID folderId);

  void hardDeleteFilesBefore(LocalDateTime cutoffTime);
}
