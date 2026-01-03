package com.study.studypal.chat.service.api.impl;

import com.study.studypal.chat.dto.message.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.message.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.message.request.UpdateMessageRequestDto;
import com.study.studypal.chat.dto.message.response.ListMessageResponseDto;
import com.study.studypal.chat.service.api.ChatService;
import com.study.studypal.common.dto.ActionResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

  @Override
  public ActionResponseDto sendMessage(
      UUID userId, UUID teamId, SendMessageRequestDto request, List<MultipartFile> attachments) {
    return null;
  }

  @Override
  public ListMessageResponseDto getMessages(UUID teamId, LocalDateTime cursor, int size) {
    return null;
  }

  @Override
  public ActionResponseDto updateMessage(UUID userId, UUID messageId, UpdateMessageRequestDto dto) {
    return null;
  }

  @Override
  public ActionResponseDto markMessagesAsRead(
      UUID userId, UUID teamId, MarkMessagesAsReadRequestDto dto) {
    return null;
  }

  @Override
  public ActionResponseDto deleteMessage(UUID userId, UUID messageId) {
    return null;
  }
}
