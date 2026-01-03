package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.entity.MessageReadStatus;
import com.study.studypal.chat.repository.MessageReadStatusRepository;
import com.study.studypal.chat.service.internal.MessageStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageStatusServiceImpl implements MessageStatusService {
  private final MessageReadStatusRepository statusRepository;

  @Override
  public List<MessageReadStatus> getByMessageId(UUID messageId) {
    return statusRepository.findAllByMessageId(messageId);
  }
}
