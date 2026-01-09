package com.study.studypal.notification.service.internal.impl;

import static com.study.studypal.notification.constant.NotificationConstant.FCM_DATA_KEY_ID;
import static com.study.studypal.notification.constant.NotificationConstant.FCM_DATA_KEY_TYPE;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.internal.NotificationTemplate;
import com.study.studypal.notification.entity.DeviceToken;
import com.study.studypal.notification.exception.NotificationErrorCode;
import com.study.studypal.notification.repository.DeviceTokenRepository;
import com.study.studypal.notification.service.internal.DeviceTokenInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenInternalServiceImpl implements DeviceTokenInternalService {
  private final DeviceTokenRepository deviceTokenRepository;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public void deleteDeviceTokenBefore(LocalDateTime time) {
    deviceTokenRepository.deleteAllByLastUpdatedBefore(time);
  }

  @Override
  public void sendPushNotification(List<UUID> recipients, NotificationTemplate template) {
    List<DeviceToken> tokens = new ArrayList<>();
    for (UUID recipient : recipients) {
      DeviceToken token = deviceTokenRepository.findFirstByUserIdOrderByLastUpdatedDesc(recipient);
      if (token != null) {
        tokens.add(token);
      }
    }

    List<String> fcmTokens = tokens.stream().map(DeviceToken::getToken).toList();
    MulticastMessage multicastMessage = buildMulticastMessage(fcmTokens, template);

    try {
      BatchResponse batchResponse =
          FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
      handleResponse(tokens, batchResponse);
    } catch (FirebaseMessagingException ex) {
      log.error("Failed to send FCM messages: {}", ex.getMessage(), ex);
      throw new BaseException(NotificationErrorCode.SEND_BATCH_MESSAGE_FAILED, ex.getMessage());
    }
  }

  private MulticastMessage buildMulticastMessage(
      List<String> tokens, NotificationTemplate template) {
    Notification notification = modelMapper.map(template, Notification.class);

    String id = template.getSubjectId() != null ? template.getSubjectId().toString() : "";

    Map<String, String> data = new HashMap<>();
    data.put(FCM_DATA_KEY_TYPE, template.getSubject().toString());
    data.put(FCM_DATA_KEY_ID, id);

    return MulticastMessage.builder()
        .setNotification(notification)
        .putAllData(data)
        .addAllTokens(tokens)
        .build();
  }

  private void handleResponse(List<DeviceToken> tokens, BatchResponse batchResponse) {
    List<SendResponse> responses = batchResponse.getResponses();
    for (int i = 0; i < responses.size(); i++) {
      SendResponse response = responses.get(i);
      if (isInvalidFcmToken(response)) {
        deviceTokenRepository.delete(tokens.get(i));
      }
    }
  }

  private boolean isInvalidFcmToken(SendResponse response) {
    if (response.isSuccessful()) {
      return false;
    }

    FirebaseMessagingException ex = response.getException();
    if (ex == null) {
      return false;
    }

    ErrorCode errorCode = ex.getErrorCode();
    return errorCode == ErrorCode.NOT_FOUND || errorCode == ErrorCode.INVALID_ARGUMENT;
  }
}
