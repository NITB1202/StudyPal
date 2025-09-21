package com.study.studypal.notification.service.internal.impl;

import com.study.studypal.notification.dto.internal.CreateNotificationDto;
import com.study.studypal.notification.entity.Notification;
import com.study.studypal.notification.repository.NotificationRepository;
import com.study.studypal.notification.service.internal.NotificationInternalService;
import java.time.LocalDateTime;

import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationInternalServiceImpl implements NotificationInternalService {
  private final NotificationRepository notificationRepository;

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  public void createNotification(CreateNotificationDto request) {
      User user = entityManager.getReference(User.class, request.getUserId());

      Notification notification = Notification.builder()
              .user(user)
              .title(request.getTitle())
              .content(request.getContent())
              .createdAt(LocalDateTime.now())
              .isRead(false)
              .subject(request.getSubject())
              .subjectId(request.getSubjectId())
              .build();

      notificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void deleteNotificationBefore(LocalDateTime time) {
      notificationRepository.deleteByCreatedAtBefore(time);
  }
}
