package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.event.MessageSentEvent;
import com.study.studypal.chat.service.internal.ChatNotificationService;
import com.study.studypal.chat.service.internal.ChatWebSocketHandler;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNotificationServiceImpl implements ChatNotificationService {
  private final TeamMembershipInternalService memberService;
  private final ApplicationEventPublisher eventPublisher;
  private final ChatWebSocketHandler handler;

  @Override
  public List<UUID> getOfflineMemberIds(UUID teamId) {
    List<UUID> memberIds = memberService.getMemberIds(teamId);
    List<UUID> offlineMemberIds = new ArrayList<>();

    for (UUID memberId : memberIds) {
      if (!handler.isUserInTeam(memberId, teamId)) {
        offlineMemberIds.add(memberId);
      }
    }

    return offlineMemberIds;
  }

  @Override
  public void publishNewMessageNotification(Message message) {
    UUID userId = message.getUser().getId();
    UUID teamId = message.getTeam().getId();

    MessageSentEvent event = MessageSentEvent.builder().userId(userId).teamId(teamId).build();

    eventPublisher.publishEvent(event);
  }
}
