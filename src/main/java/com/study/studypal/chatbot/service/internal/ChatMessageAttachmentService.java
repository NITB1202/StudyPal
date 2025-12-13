package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ChatMessageAttachmentService {
  List<ChatMessageAttachment> getByMessageId(UUID messageId);

  List<String> validateAndSerializeAttachments(List<MultipartFile> files);
}
