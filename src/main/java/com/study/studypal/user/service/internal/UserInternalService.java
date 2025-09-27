package com.study.studypal.user.service.internal;

import com.study.studypal.user.dto.internal.UserSummaryProfile;
import java.util.UUID;

public interface UserInternalService {
  UUID createDefaultProfile(String name);

  UUID createProfile(String name, String avatarUrl);

  UserSummaryProfile getUserSummaryProfile(UUID userId);
}
