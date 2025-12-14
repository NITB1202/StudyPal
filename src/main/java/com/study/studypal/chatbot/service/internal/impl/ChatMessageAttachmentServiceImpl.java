package com.study.studypal.chatbot.service.internal.impl;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import com.study.studypal.chatbot.exception.ChatMessageAttachmentErrorCode;
import com.study.studypal.chatbot.repository.ChatMessageAttachmentRepository;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.extractor.TextExtractor;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.util.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatMessageAttachmentServiceImpl implements ChatMessageAttachmentService {
  private final ChatMessageAttachmentRepository attachmentRepository;
  private final ChatbotProperties props;
  private final TextExtractor extractor;

  @Override
  public List<ChatMessageAttachment> getByMessageId(UUID messageId) {
    return attachmentRepository.findByMessageId(messageId);
  }

  @Override
  @Transactional
  public List<String> validateAndSerializeAttachments(List<MultipartFile> files) {
    if (CollectionUtils.isEmpty(files)) return List.of();

    long totalFileSize = 0;
    List<String> result = new ArrayList<>();

    for (MultipartFile file : files) {
      // Validate document type
      if (!FileUtils.isDocument(file)) {
        throw new BaseException(ChatMessageAttachmentErrorCode.ATTACHMENT_UNSUPPORTED_TYPE);
      }

      // Validate single file size
      long fileSize = file.getSize();
      if (fileSize > props.getMaxFileSize().toBytes()) {
        throw new BaseException(ChatMessageAttachmentErrorCode.ATTACHMENT_SIZE_EXCEEDED);
      }

      // Validate total size
      totalFileSize += fileSize;
      if (totalFileSize > props.getMaxTotalSize().toBytes()) {
        throw new BaseException(ChatMessageAttachmentErrorCode.ATTACHMENT_TOTAL_SIZE_EXCEEDED);
      }

      // Extract and normalize content
      String content = extractor.extract(file);
      result.add(content);
    }

    return result;
  }
}
