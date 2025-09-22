package com.study.studypal.user.service.internal;

import com.study.studypal.user.dto.internal.UserSummaryProfileDto;
import java.util.UUID;

public interface UserInternalService {
  UUID createDefaultProfile(String name);

  UUID createProfile(String name, String avatarUrl);

  UserSummaryProfileDto getUserSummaryProfile(UUID userId);
}
