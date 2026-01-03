package com.study.studypal.chat.service.internal;

import com.study.studypal.chat.dto.request.EditMessageRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageService {
  Message saveMessage(UUID userId, UUID teamId, SendMessageRequestDto request);

  List<Message> getMessages(UUID teamId, LocalDateTime cursor, int size);

  Long countMessages(UUID teamId);

  Message getByIdWithTeam(UUID id);

  List<Message> getMessagesBefore(LocalDateTime time);

  List<Message> getMessagesBefore(UUID teamId, LocalDateTime time);

  Message editMessage(UUID userId, UUID messageId, EditMessageRequestDto request);

  Message deleteMessage(UUID userId, UUID messageId);

  void hardDeleteMessages(List<Message> messages);
}
