package com.study.studypal.chat.service.api.impl;

import com.study.studypal.chat.dto.internal.EditMessageEventData;
import com.study.studypal.chat.dto.request.EditMessageRequestDto;
import com.study.studypal.chat.dto.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.response.ListMessageResponseDto;
import com.study.studypal.chat.dto.response.MessageAttachmentResponseDto;
import com.study.studypal.chat.dto.response.MessageResponseDto;
import com.study.studypal.chat.dto.response.MessageUserResponseDto;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageAttachment;
import com.study.studypal.chat.entity.MessageReadStatus;
import com.study.studypal.chat.enums.ChatEventType;
import com.study.studypal.chat.service.api.ChatService;
import com.study.studypal.chat.service.internal.ChatWebSocketHandler;
import com.study.studypal.chat.service.internal.MessageAttachmentService;
import com.study.studypal.chat.service.internal.MessageService;
import com.study.studypal.chat.service.internal.MessageStatusService;
import com.study.studypal.common.dto.ActionResponseDto;
import com.study.studypal.team.service.internal.TeamMembershipInternalService;
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
  private final MessageStatusService messageStatusService;
  private final ChatWebSocketHandler handler;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public ActionResponseDto sendMessage(
      UUID userId, UUID teamId, SendMessageRequestDto request, List<MultipartFile> attachments) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    Message savedMessage = messageService.saveMessage(userId, teamId, request);
    List<MessageAttachment> savedAttachments =
        attachmentService.saveAttachments(savedMessage, attachments);

    MessageResponseDto chatMessage = toMessageResponse(savedMessage, savedAttachments, List.of());
    handler.sendMessageToOnlineMembers(teamId, ChatEventType.SEND, chatMessage);

    return ActionResponseDto.builder().success(true).message("Send successfully.").build();
  }

  @Override
  public ListMessageResponseDto getMessages(
      UUID userId, UUID teamId, LocalDateTime cursor, int size) {
    memberService.validateUserBelongsToTeam(userId, teamId);

    List<Message> messages = messageService.getMessages(teamId, cursor, size);
    List<MessageResponseDto> responseDto =
        messages.stream()
            .map(
                message -> {
                  List<MessageAttachment> attachments =
                      attachmentService.getByMessageId(message.getId());
                  List<MessageReadStatus> readStatuses =
                      messageStatusService.getByMessageId(message.getId());
                  return toMessageResponse(message, attachments, readStatuses);
                })
            .toList();

    long total = messageService.countMessages(teamId);

    LocalDateTime nextCursor =
        messages.size() == size ? messages.get(messages.size() - 1).getCreatedAt() : null;

    return ListMessageResponseDto.builder()
        .messages(responseDto)
        .total(total)
        .nextCursor(nextCursor)
        .build();
  }

  @Override
  public ActionResponseDto editMessage(UUID userId, UUID messageId, EditMessageRequestDto request) {
    Message message = messageService.editMessage(userId, messageId, request);

    EditMessageEventData data = modelMapper.map(message, EditMessageEventData.class);
    handler.sendMessageToOnlineMembers(message.getTeam().getId(), ChatEventType.EDIT, data);

    return ActionResponseDto.builder().success(true).message("Edit successfully.").build();
  }

  @Override
  public ActionResponseDto markMessagesAsRead(
      UUID userId, UUID teamId, MarkMessagesAsReadRequestDto request) {
    messageService.markMessagesAsRead(userId, teamId, request);

    //    MarkMessagesEventData data = modelMapper.map();
    //    handler.sendMessageToOnlineMembers(teamId, ChatEventType.MARK, data);

    return ActionResponseDto.builder().success(true).message("Mark successfully.").build();
  }

  @Override
  public ActionResponseDto deleteMessage(UUID userId, UUID messageId) {
    return null;
  }

  private MessageResponseDto toMessageResponse(
      Message message, List<MessageAttachment> attachments, List<MessageReadStatus> readStatuses) {
    MessageResponseDto response = modelMapper.map(message, MessageResponseDto.class);

    MessageUserResponseDto userResponse =
        modelMapper.map(message.getUser(), MessageUserResponseDto.class);
    response.setUser(userResponse);

    List<MessageAttachmentResponseDto> attachmentsResponse =
        modelMapper.map(
            attachments, new TypeToken<List<MessageAttachmentResponseDto>>() {}.getType());
    response.setAttachments(attachmentsResponse);

    List<MessageUserResponseDto> readBy =
        readStatuses.stream()
            .map(
                messageReadStatus ->
                    modelMapper.map(messageReadStatus.getUser(), MessageUserResponseDto.class))
            .toList();
    response.setReadBy(readBy);

    return response;
  }
}
