package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.chat.event.UserMentionedEvent;
import com.study.studypal.chat.service.internal.ChatNotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNotificationServiceImpl implements ChatNotificationService {
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void publishNewMessageNotification(Message message) {
    UUID userId = message.getUser().getId();
    UUID teamId = message.getTeam().getId();

    MessageSentEvent event = MessageSentEvent.builder().userId(userId).teamId(teamId).build();

    eventPublisher.publishEvent(event);
  }

  @Override
  public void publishUserMentionedNotification(UUID userId, UUID teamId, List<UUID> memberIds) {
    UserMentionedEvent event =
        UserMentionedEvent.builder().userId(userId).teamId(teamId).memberIds(memberIds).build();

    eventPublisher.publishEvent(event);
  }
}
