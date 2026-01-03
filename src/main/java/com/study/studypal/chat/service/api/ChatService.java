package com.study.studypal.chat.service.api;

import com.study.studypal.chat.dto.request.EditMessageRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.response.ListMessageResponseDto;
import com.study.studypal.common.dto.ActionResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ChatService {
  ActionResponseDto sendMessage(
      UUID userId, UUID teamId, SendMessageRequestDto request, List<MultipartFile> attachments);

  ListMessageResponseDto getMessages(UUID userId, UUID teamId, LocalDateTime cursor, int size);

  ActionResponseDto editMessage(UUID userId, UUID messageId, EditMessageRequestDto request);

  ActionResponseDto markMessageAsRead(UUID userId, UUID messageId);

  ActionResponseDto deleteMessage(UUID userId, UUID messageId);
}
