package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.notification.dto.internal.CreateNotificationDto;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationInternalServiceImpl implements NotificationInternalService {
  private final NotificationRepository notificationRepository;

  @Override
  public void createNotification(CreateNotificationDto request) {}

  @Override
  public void deleteNotificationBefore(LocalDateTime time) {}
}
