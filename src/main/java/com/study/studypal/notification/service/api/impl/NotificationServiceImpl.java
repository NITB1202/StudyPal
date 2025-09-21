package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.notification.dto.notification.request.DeleteNotificationsRequestDto;
import com.study.studypal.notification.dto.notification.request.MarkNotificationsAsReadRequestDto;
import com.study.studypal.notification.dto.notification.response.ListNotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.NotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.UnreadNotificationCountResponseDto;
import com.study.studypal.notification.entity.Notification;
import com.study.studypal.notification.exception.NotificationErrorCode;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.api.NotificationService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final ModelMapper modelMapper;

  @Override
  public ListNotificationResponseDto getNotifications(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    List<Notification> notifications =
        notificationRepository.findByUserIdWithCursor(userId, cursor, pageable);
    List<NotificationResponseDto> notificationsDTO =
        modelMapper.map(notifications, new TypeToken<List<NotificationResponseDto>>() {}.getType());

    long total = notificationRepository.countByUserId(userId);
    LocalDateTime nextCursor =
        !notifications.isEmpty() && notifications.size() == size
            ? notifications.get(notifications.size() - 1).getCreatedAt()
            : null;

    return ListNotificationResponseDto.builder()
        .notifications(notificationsDTO)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public UnreadNotificationCountResponseDto getUnreadNotificationCount(UUID userId) {
    long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
    return UnreadNotificationCountResponseDto.builder().count(count).build();
  }

  @Override
  @Transactional
  public ActionResponseDto markNotificationsAsRead(
      UUID userId, MarkNotificationsAsReadRequestDto request) {
    int updatedCount = notificationRepository.markAsReadByIds(userId, request.getIds());
    if (updatedCount != request.getIds().size()) {
      throw new BaseException(NotificationErrorCode.PERMISSION_UPDATE_NOTIFICATION_DENIED);
    }
    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  public ActionResponseDto markAllNotificationsAsRead(UUID userId) {
    notificationRepository.markAllAsRead(userId);
    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  @Transactional
  public ActionResponseDto deleteNotifications(UUID userId, DeleteNotificationsRequestDto request) {
    int deletedCount = notificationRepository.deleteByIds(userId, request.getIds());
    if (deletedCount != request.getIds().size()) {
      throw new BaseException(NotificationErrorCode.PERMISSION_DELETE_NOTIFICATION_DENIED);
    }
    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  @Override
  @Transactional
  public ActionResponseDto deleteAllNotifications(UUID userId) {
    notificationRepository.deleteAllByUserId(userId);
    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }
}
