package com.study.studypal.chat.service.internal.impl;

import static com.study.studypal.chat.constant.ChatConstant.CHAT_FOLDER;
import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_IMAGE;
import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_RAW;
import static com.study.studypal.common.util.Constants.RESOURCE_TYPE_VIDEO;

import com.study.studypal.chat.config.ChatProperties;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageAttachment;
import com.study.studypal.chat.enums.FileType;
import com.study.studypal.chat.exception.MessageAttachmentErrorCode;
import com.study.studypal.chat.repository.MessageAttachmentRepository;
import com.study.studypal.chat.service.internal.MessageAttachmentService;
import com.study.studypal.common.dto.FileResponse;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.common.exception.code.FileErrorCode;
import com.study.studypal.common.service.FileService;
import com.study.studypal.common.util.FileUtils;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MessageAttachmentServiceImpl implements MessageAttachmentService {
  private final MessageAttachmentRepository attachmentRepository;
  private final FileService fileService;
  private final ChatProperties props;

  @Override
  public List<MessageAttachment> saveAttachments(Message message, List<MultipartFile> files) {
    if (CollectionUtils.isEmpty(files)) return List.of();

    List<UUID> ids = new ArrayList<>();
    List<MessageAttachment> attachments = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();
    String folderPath = String.format("%s/%s", CHAT_FOLDER, message.getId());

    long totalFileSize = 0;

    for (MultipartFile file : files) {
      try {
        long fileSize = file.getSize();
        validateFileSize(fileSize, totalFileSize);
        totalFileSize += fileSize;

        UUID id = UUID.randomUUID();
        FileResponse fileResponse =
            fileService.uploadFile(folderPath, id.toString(), file.getBytes());
        MessageAttachment attachment = buildAttachment(id, message, file, fileResponse, now);

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

    return attachmentRepository.saveAll(attachments);
  }

  @Override
  public List<MessageAttachment> getAttachmentsByMessageId(UUID messageId) {
    return attachmentRepository.findAllByMessageId(messageId);
  }

  @Transactional
  @Override
  public void deleteAttachmentsByMessageId(UUID messageId) {
    List<MessageAttachment> attachments = attachmentRepository.findAllByMessageId(messageId);

    for (MessageAttachment attachment : attachments) {
      String resourceType = getResourceType(attachment.getType());
      fileService.deleteFile(attachment.getId().toString(), resourceType);
    }

    attachmentRepository.deleteAll(attachments);
  }

  private void validateFileSize(long fileSize, long totalFileSize) {
    if (fileSize > props.getMaxFileSize().toBytes()) {
      throw new BaseException(MessageAttachmentErrorCode.ATTACHMENT_SIZE_EXCEEDED);
    }

    if (totalFileSize + fileSize > props.getMaxTotalSize().toBytes()) {
      throw new BaseException(MessageAttachmentErrorCode.ATTACHMENT_TOTAL_SIZE_EXCEEDED);
    }
  }

  private MessageAttachment buildAttachment(
      UUID id,
      Message message,
      MultipartFile file,
      FileResponse uploadResponse,
      LocalDateTime uploadedAt) {
    return MessageAttachment.builder()
        .id(id)
        .message(message)
        .type(getFileType(file))
        .name(file.getOriginalFilename())
        .url(uploadResponse.getUrl())
        .size(uploadResponse.getBytes())
        .uploadedAt(uploadedAt)
        .build();
  }

  private FileType getFileType(MultipartFile file) {
    if (FileUtils.isImage(file)) {
      return FileType.IMAGE;
    }

    if (FileUtils.isVideo(file)) {
      return FileType.VIDEO;
    }

    return FileType.FILE;
  }

  private String getResourceType(FileType fileType) {
    return switch (fileType) {
      case IMAGE -> RESOURCE_TYPE_IMAGE;
      case VIDEO -> RESOURCE_TYPE_VIDEO;
      case FILE -> RESOURCE_TYPE_RAW;
    };
  }
}
