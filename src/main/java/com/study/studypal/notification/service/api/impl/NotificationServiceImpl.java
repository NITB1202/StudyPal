package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.notification.dto.notification.request.DeleteNotificationsRequestDto;
import com.study.studypal.notification.dto.notification.request.MarkNotificationsAsReadRequestDto;
import com.study.studypal.notification.dto.notification.response.ListNotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.UnreadNotificationCountResponseDto;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.api.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;

  @Override
  public ListNotificationResponseDto getNotifications(UUID userId, LocalDateTime cursor, int size) {
    return null;
  }

  @Override
  public UnreadNotificationCountResponseDto getUnreadNotificationCount(UUID userId) {
    return null;
  }

  @Override
  public ActionResponseDto markNotificationsAsRead(MarkNotificationsAsReadRequestDto request) {
    return null;
  }

  @Override
  public ActionResponseDto markAllNotificationsAsRead(UUID userId) {
    return null;
  }

  @Override
  public ActionResponseDto deleteNotifications(UUID userId, DeleteNotificationsRequestDto request) {
    return null;
  }

  @Override
  public ActionResponseDto deleteAllNotifications(UUID userId) {
    return null;
  }
}
