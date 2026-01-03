package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.entity.MessageReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface MessageStatusService {
  @EntityGraph(attributePaths = {"user"})
  List<MessageReadStatus> getByMessageId(UUID messageId);
}
