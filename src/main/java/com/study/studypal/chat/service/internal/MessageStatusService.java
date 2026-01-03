package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageReadStatus;
import java.util.List;
import java.util.UUID;

public interface MessageStatusService {
  List<MessageReadStatus> getByMessageId(UUID messageId);

  MessageReadStatus markMessageAsRead(UUID userId, Message message);

  void markMessagesAsRead(UUID userId, List<Message> messages);
}
