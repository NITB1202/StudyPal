package com.study.studypal.chatbot.service.internal;

import com.study.studypal.chatbot.dto.external.ExtractedFile;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ChatMessageAttachmentService {
  List<ChatMessageAttachment> getAttachmentsByMessageId(UUID messageId);

  List<ExtractedFile> validateAndExtractAttachments(List<MultipartFile> files);

  void saveAttachments(ChatMessage message, List<MultipartFile> files);

  void deleteAttachmentsByMessageId(UUID messageId);
}
