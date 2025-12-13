package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import com.study.studypal.chatbot.repository.ChatMessageAttachmentRepository;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageAttachmentServiceImpl implements ChatMessageAttachmentService {
  private final ChatMessageAttachmentRepository attachmentRepository;

  @Override
  public List<ChatMessageAttachment> getByMessageId(UUID messageId) {
    return attachmentRepository.findByMessageId(messageId);
  }
}
