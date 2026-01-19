package com.study.studypal.notification.service.api.impl;

import com.study.studypal.common.cache.CacheNames;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.notification.dto.notification.request.DeleteNotificationsRequestDto;
import com.study.studypal.notification.dto.notification.request.MarkNotificationsAsReadRequestDto;
import com.study.studypal.notification.dto.notification.response.ListNotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.NotificationResponseDto;
import com.study.studypal.notification.dto.notification.response.UnreadNotificationCountResponseDto;
import com.study.studypal.notification.entity.Notification;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.api.NotificationService;
import com.study.studypal.team.service.internal.InvitationInternalService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final InvitationInternalService invitationService;
  private final ModelMapper modelMapper;

  @Override
  @Cacheable(
      value = CacheNames.NOTIFICATIONS,
      key = "@keys.of(#userId)",
      condition = "#cursor == null && #size == 10")
  public ListNotificationResponseDto getNotifications(UUID userId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    List<Notification> notifications =
        cursor == null
            ? notificationRepository.findByUserId(userId, pageable)
            : notificationRepository.findByUserIdWithCursor(userId, cursor, pageable);
    List<NotificationResponseDto> notificationsDTO =
        modelMapper.map(notifications, new TypeToken<List<NotificationResponseDto>>() {}.getType());

    long total = notificationRepository.countByUserId(userId);
    LocalDateTime nextCursor =
        notifications.size() == size
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
    long notificationCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
    long invitationCount = invitationService.countByUserId(userId);

    return UnreadNotificationCountResponseDto.builder()
        .count(notificationCount + invitationCount)
        .build();
  }

  @Override
  @CacheEvict(value = CacheNames.NOTIFICATIONS, key = "@keys.of(#userId)")
  public ActionResponseDto markNotificationsAsRead(
      UUID userId, MarkNotificationsAsReadRequestDto request) {
    notificationRepository.markAsReadByIds(userId, request.getIds());
    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  @CacheEvict(value = CacheNames.NOTIFICATIONS, key = "@keys.of(#userId)")
  public ActionResponseDto markAllNotificationsAsRead(UUID userId) {
    notificationRepository.markAllAsRead(userId);
    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  @CacheEvict(value = CacheNames.NOTIFICATIONS, key = "@keys.of(#userId)")
  public ActionResponseDto deleteNotifications(UUID userId, DeleteNotificationsRequestDto request) {
    notificationRepository.deleteByIds(userId, request.getIds());
    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }

  @Override
  @CacheEvict(value = CacheNames.NOTIFICATIONS, key = "@keys.of(#userId)")
  public ActionResponseDto deleteAllNotifications(UUID userId) {
    notificationRepository.deleteAllByUserId(userId);
    return ActionResponseDto.builder().success(true).message("Delete successfully.").build();
  }
}
