package com.study.studypal.notification.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.notification.dto.token.RegisterDeviceTokenRequestDto;

import java.util.UUID;

public interface DeviceTokenService {
  ActionResponseDto registerDeviceToken(UUID userId, RegisterDeviceTokenRequestDto request);

  ActionResponseDto removeDeviceToken(UUID userId, String token);
}
