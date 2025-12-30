package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.entity.TeamUsage;
import com.study.studypal.file.entity.UserUsage;
import java.util.UUID;

public interface UsageService {
  void createUserUsage(UUID userId);

  void createTeamUsage(UUID teamId);

  UserUsage getUserUsage(UUID userId);

  TeamUsage getTeamUsage(UUID teamId);

  void validateUsage(Folder folder, long fileSize);

  void increaseUsage(Folder folder, long fileSize);

  void decreaseUsage(Folder folder, long fileSize);
}
