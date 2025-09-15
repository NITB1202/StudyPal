package com.study.studypal.user.service.internal;

import java.util.UUID;

public interface UserInternalService {
  UUID createDefaultProfile(String name);

  UUID createProfile(String name, String avatarUrl);
}
