package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.notification.repository.DeviceTokenRepository;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenInternalServiceImpl implements DeviceTokenInternalService {
  private final DeviceTokenRepository deviceTokenRepository;

  @Override
  @Transactional
  public void deleteDeviceTokenBefore(LocalDateTime time) {
    deviceTokenRepository.deleteAllByLastUpdatedBefore(time);
  }
}
