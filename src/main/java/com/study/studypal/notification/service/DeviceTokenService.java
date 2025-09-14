package com.study.studypal.notification.service;

import com.study.studypal.common.dto.ActionResponseDto;
import java.util.UUID;

public interface DeviceTokenService {
  ActionResponseDto registerDeviceToken(UUID userId, String token);

  ActionResponseDto removeDeviceToken(String token);
}
