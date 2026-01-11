package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.Message;

public interface ChatNotificationService {
  void publishNewMessageNotification(Message message);
}
