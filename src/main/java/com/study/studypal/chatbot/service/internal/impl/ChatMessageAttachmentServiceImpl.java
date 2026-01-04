package com.study.studypal.chatbot.service.internal.impl;

import static com.study.studypal.chatbot.constant.ChatbotConstant.CHATBOT_FOLDER;

import com.study.studypal.chatbot.config.ChatbotProperties;
import com.study.studypal.chatbot.dto.external.ExtractedFile;
import com.study.studypal.chatbot.entity.ChatMessage;
import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import com.study.studypal.chatbot.exception.ChatMessageAttachmentErrorCode;
import com.study.studypal.chatbot.repository.ChatMessageAttachmentRepository;
import com.study.studypal.chatbot.service.internal.ChatMessageAttachmentService;
import com.study.studypal.chatbot.service.internal.extractor.TextExtractor;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import java.io.IOException;
import java.time.LocalDateTime;
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
  private final FileService fileService;

  @Override
  public List<ChatMessageAttachment> getAttachmentsByMessageId(UUID messageId) {
    return attachmentRepository.findByMessageId(messageId);
  }

  @Override
  @Transactional
  public List<ExtractedFile> validateAndExtractAttachments(List<MultipartFile> files) {
    if (CollectionUtils.isEmpty(files)) return List.of();

    long totalFileSize = 0;
    List<ExtractedFile> result = new ArrayList<>();

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
      String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
      String content = extractor.extract(file);

      ExtractedFile extractedFile =
          ExtractedFile.builder().fileName(fileName).content(content).build();

      result.add(extractedFile);
    }

    return result;
  }

  @Override
  @Transactional
  public void saveAttachments(ChatMessage message, List<MultipartFile> files) {
    if (CollectionUtils.isEmpty(files)) return;

    List<UUID> ids = new ArrayList<>();
    List<ChatMessageAttachment> attachments = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();
    String folderPath = String.format("%s/%s", CHATBOT_FOLDER, message.getId());

    for (MultipartFile file : files) {
      try {
        UUID id = UUID.randomUUID();
        FileResponse fileResponse =
            fileService.uploadFile(folderPath, id.toString(), file.getBytes());

        ChatMessageAttachment attachment =
            ChatMessageAttachment.builder()
                .id(id)
                .chatMessage(message)
                .name(file.getOriginalFilename())
                .url(fileResponse.getUrl())
                .size(fileResponse.getBytes())
                .uploadedAt(now)
                .build();

        ids.add(id);
        attachments.add(attachment);
      } catch (IOException e) {
        ids.forEach(
            id -> {
              String extension = FileUtils.extractFileExtension(file);
              String resourceType = fileService.getResourceType(extension);
              fileService.deleteFile(id.toString(), resourceType);
            });

        throw new BaseException(FileErrorCode.INVALID_FILE_CONTENT);
      }
    }

    attachmentRepository.saveAll(attachments);
  }

  @Override
  @Transactional
  public void deleteAttachmentsByMessageId(UUID messageId) {
    List<ChatMessageAttachment> attachments = attachmentRepository.findByMessageId(messageId);

    for (ChatMessageAttachment attachment : attachments) {
      String extension = FileUtils.extractFileExtension(attachment.getName());
      String resourceType = fileService.getResourceType(extension);
      fileService.deleteFile(attachment.getId().toString(), resourceType);
    }

    attachmentRepository.deleteAll(attachments);
  }
}
