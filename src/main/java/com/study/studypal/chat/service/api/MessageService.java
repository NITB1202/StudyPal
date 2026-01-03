package com.study.studypal.chat.service.api;

import com.study.studypal.chat.dto.message.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.message.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.message.request.UpdateMessageRequestDto;
import com.study.studypal.chat.dto.message.response.ListMessageResponseDto;
import com.study.studypal.common.dto.ActionResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
  ActionResponseDto sendMessage(
      UUID userId, UUID teamId, SendMessageRequestDto request, MultipartFile file);

  ListMessageResponseDto getMessages(UUID teamId, LocalDateTime cursor, int size);

  ActionResponseDto updateMessage(UUID userId, UUID messageId, UpdateMessageRequestDto dto);

  ActionResponseDto markMessagesAsRead(UUID userId, UUID teamId, MarkMessagesAsReadRequestDto dto);

  ActionResponseDto deleteMessage(UUID userId, UUID messageId);
}
