package com.study.studypal.chat.service.api.impl;

import com.study.studypal.chat.dto.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.request.UpdateMessageRequestDto;
import com.study.studypal.chat.dto.response.ListMessageResponseDto;
import com.study.studypal.chat.dto.response.MessageAttachmentResponseDto;
import com.study.studypal.chat.dto.response.MessageResponseDto;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageAttachment;
import com.study.studypal.chat.service.api.ChatService;
import com.study.studypal.chat.service.internal.MessageAttachmentService;
import com.study.studypal.chat.service.internal.MessageService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
import com.study.studypal.user.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final MessageService messageService;
  private final MessageAttachmentService attachmentService;
  private final TeamMembershipInternalService memberService;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public ActionResponseDto sendMessage(
      UUID userId, UUID teamId, SendMessageRequestDto request, List<MultipartFile> attachments) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    Message message = messageService.saveMessage(userId, teamId, request);
    List<MessageAttachment> uploadedAttachments =
        attachmentService.saveAttachments(message, attachments);

    MessageResponseDto response = toMessageResponse(message, uploadedAttachments);

    return ActionResponseDto.builder().success(true).message("Send successfully.").build();
  }

  @Override
  public ListMessageResponseDto getMessages(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
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

  private MessageResponseDto toMessageResponse(
      Message message, List<MessageAttachment> attachments) {
    MessageResponseDto response = modelMapper.map(message, MessageResponseDto.class);

    User user = message.getUser();
    response.setUserId(user.getId());
    response.setName(user.getName());
    response.setAvatarUrl(user.getAvatarUrl());

    List<MessageAttachmentResponseDto> attachmentsResponse =
        modelMapper.map(
            attachments, new TypeToken<List<MessageAttachmentResponseDto>>() {}.getType());
    response.setAttachments(attachmentsResponse);

    return response;
  }
}
