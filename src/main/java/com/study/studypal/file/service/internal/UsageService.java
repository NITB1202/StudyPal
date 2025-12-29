package com.study.studypal.file.service.internal;

import com.study.studypal.file.entity.Folder;
import com.study.studypal.file.entity.TeamUsage;
import com.study.studypal.file.entity.UserUsage;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UsageService {
  void createUserUsage(UUID userId);

  void createTeamUsage(UUID teamId);

  UserUsage getUserUsage(UUID userId);

  TeamUsage getTeamUsage(UUID teamId);

  void validateUsage(Folder folder, MultipartFile file);
}
