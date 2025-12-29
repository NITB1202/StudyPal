package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.Folder;
import java.util.UUID;

public interface FolderInternalService {
  void createTeamDefaultFolder(UUID userId, UUID teamId);

  Folder getById(UUID id);
}
