package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.token.RegisterDeviceTokenRequestDto;
import com.study.studypal.notification.entity.DeviceToken;
import com.study.studypal.notification.exception.DeviceTokenErrorCode;
import com.study.studypal.notification.repository.DeviceTokenRepository;
import com.study.studypal.notification.service.api.DeviceTokenService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {
  private final DeviceTokenRepository deviceTokenRepository;

  @PersistenceContext private final EntityManager entityManager;

  @Override
  public ActionResponseDto registerDeviceToken(UUID userId, RegisterDeviceTokenRequestDto request) {
    DeviceToken deviceToken =
        deviceTokenRepository.findByUserIdAndToken(userId, request.getDeviceToken());

    if (deviceToken == null) {
      User user = entityManager.getReference(User.class, userId);
      deviceToken =
          DeviceToken.builder()
              .user(user)
              .platform(request.getPlatform())
              .token(request.getDeviceToken())
              .build();
    }

    deviceToken.setLastUpdated(LocalDateTime.now());
    deviceTokenRepository.save(deviceToken);

    return ActionResponseDto.builder().success(true).message("Register successfully.").build();
  }

  @Override
  public ActionResponseDto removeDeviceToken(UUID userId, String token) {
    DeviceToken deviceToken = deviceTokenRepository.findByUserIdAndToken(userId, token);

    if (deviceToken == null) {
      throw new BaseException(DeviceTokenErrorCode.DEVICE_TOKEN_NOT_FOUND);
    }

    deviceTokenRepository.delete(deviceToken);

    return ActionResponseDto.builder().success(true).message("Remove successfully.").build();
  }
}
