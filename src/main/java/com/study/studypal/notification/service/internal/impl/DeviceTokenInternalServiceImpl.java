package com.study.studypal.notification.service.internal.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.study.studypal.notification.dto.internal.CreateNotificationDto;
import com.study.studypal.notification.entity.DeviceToken;
import com.study.studypal.notification.repository.DeviceTokenRepository;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenInternalServiceImpl implements DeviceTokenInternalService {
  private final DeviceTokenRepository deviceTokenRepository;

  @Override
  @Transactional
  public void deleteDeviceTokenBefore(LocalDateTime time) {
    deviceTokenRepository.deleteAllByLastUpdatedBefore(time);
  }

  @Override
  public void sendPushNotification(CreateNotificationDto dto) {
    List<DeviceToken> tokens = deviceTokenRepository.findByUserId(dto.getUserId());
    String id = dto.getSubjectId() != null ? dto.getSubjectId().toString() : "";

    Map<String, String> data = new HashMap<>();
    data.put("type", dto.getSubject().toString());
    data.put("id", id);

    Notification notification =
        Notification.builder().setTitle(dto.getTitle()).setBody(dto.getContent()).build();

    for (DeviceToken token : tokens) {
      Message message =
          Message.builder()
              .setToken(token.getToken())
              .setNotification(notification)
              .putAllData(data)
              .build();
      try {
        FirebaseMessaging.getInstance().send(message);
      } catch (FirebaseMessagingException e) {
        MessagingErrorCode firebaseErrorCode = e.getMessagingErrorCode();
        if (isInvalidFmcToken(firebaseErrorCode)) {
          deviceTokenRepository.delete(token);
          log.info("Invalid fmc token: {}", token.getToken());
        } else {
          log.error("Failed to send push notification: {}", e.getMessage());
        }
      }
    }
  }

  private boolean isInvalidFmcToken(MessagingErrorCode errorCode) {
    return errorCode == MessagingErrorCode.INVALID_ARGUMENT
        || errorCode == MessagingErrorCode.UNREGISTERED;
  }
}
