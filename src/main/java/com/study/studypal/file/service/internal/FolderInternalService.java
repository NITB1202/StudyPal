package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.File;
import com.study.studypal.file.entity.Folder;
import java.util.UUID;

public interface FolderInternalService {
  void createTeamDefaultFolder(UUID userId, UUID teamId);

  Folder getById(UUID id);

  void updateAuditFields(UUID userId, Folder folder);

  void increaseFile(UUID userId, Folder folder, File file);

  void decreaseFile(UUID userId, Folder folder, File file);

  void recoverFolder(Folder folder);
}
