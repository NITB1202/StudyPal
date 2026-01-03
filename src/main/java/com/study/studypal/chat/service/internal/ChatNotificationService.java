package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.Message;
import java.util.List;
import java.util.UUID;

public interface ChatNotificationService {
  List<UUID> getOfflineMemberIds(UUID teamId);

  void publishNewMessageNotification(Message message);
}
