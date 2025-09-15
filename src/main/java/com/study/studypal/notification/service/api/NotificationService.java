package com.study.studypal.notification.service.api;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.notification.dto.notification.request.DeleteNotificationsRequestDto;
import com.study.studypal.notification.dto.notification.request.MarkNotificationsAsReadRequestDto;
import com.study.studypal.notification.dto.notification.response.ListNotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.UnreadNotificationCountResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public interface NotificationService {
  ListNotificationResponseDto getNotifications(UUID userId, LocalDateTime cursor, int size);

  UnreadNotificationCountResponseDto getUnreadNotificationCount(UUID userId);

  ActionResponseDto markNotificationsAsRead(MarkNotificationsAsReadRequestDto request);

  ActionResponseDto markAllNotificationsAsRead(UUID userId);

  ActionResponseDto deleteNotifications(UUID userId, DeleteNotificationsRequestDto request);

  ActionResponseDto deleteAllNotifications(UUID userId);
}
