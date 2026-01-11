package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.Message;
import java.util.List;
import java.util.UUID;

public interface ChatNotificationService {
  void publishNewMessageNotification(Message message);

  void publishUserMentionedNotification(UUID userId, UUID teamId, List<UUID> memberIds);
}
