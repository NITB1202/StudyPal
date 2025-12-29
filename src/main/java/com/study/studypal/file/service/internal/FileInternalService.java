package com.study.studypal.file.service.internal;

import java.util.UUID;

public interface FileInternalService {
  void softDeleteFilesInFolder(UUID folderId);
}
