package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.config.ChatProperties;
import com.study.studypal.chat.dto.request.EditMessageRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.exception.MessageErrorCode;
import com.study.studypal.chat.repository.MessageRepository;
import com.study.studypal.chat.service.internal.MessageService;
import com.study.studypal.common.exception.BaseException;
import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
  private final MessageRepository messageRepository;
  private final ChatProperties chatProperties;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  @Transactional
  public Message saveMessage(UUID userId, UUID teamId, SendMessageRequestDto request) {
    LocalDateTime now = LocalDateTime.now();
    User user = entityManager.getReference(User.class, userId);
    Team team = entityManager.getReference(Team.class, teamId);

    Message message =
        Message.builder()
            .user(user)
            .team(team)
            .content(request.getContent())
            .createdAt(now)
            .updatedAt(now)
            .isDeleted(false)
            .build();

    return messageRepository.save(message);
  }

  @Override
  public List<Message> getMessages(UUID teamId, LocalDateTime cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);
    return cursor == null
        ? messageRepository.findByTeamId(teamId, pageable)
        : messageRepository.findByTeamIdWithCursor(teamId, cursor, pageable);
  }

  @Override
  public Long countMessages(UUID teamId) {
    return messageRepository.countByTeamId(teamId);
  }

  @Override
  public Message getByIdWithTeam(UUID id) {
    return messageRepository
        .findByIdWithTeam(id)
        .orElseThrow(() -> new BaseException(MessageErrorCode.MESSAGE_NOT_FOUND));
  }

  @Override
  public List<Message> getMessagesBefore(UUID teamId, LocalDateTime time) {
    return messageRepository.findMessagesBefore(teamId, time);
  }

  @Override
  public Message editMessage(UUID userId, UUID messageId, EditMessageRequestDto request) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new BaseException(MessageErrorCode.MESSAGE_NOT_FOUND));

    validateMessageOwnership(userId, message);

    LocalDateTime now = LocalDateTime.now();
    Duration duration = Duration.between(message.getCreatedAt(), now);

    if (duration.toSeconds() > chatProperties.getEditTimeLimitSeconds()) {
      throw new BaseException(MessageErrorCode.MESSAGE_EDIT_TIME_EXPIRED);
    }

    message.setContent(request.getContent());
    message.setUpdatedAt(now);

    return messageRepository.save(message);
  }

  @Override
  public Message deleteMessage(UUID userId, UUID messageId) {
    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(() -> new BaseException(MessageErrorCode.MESSAGE_NOT_FOUND));

    validateMessageOwnership(userId, message);

    message.setIsDeleted(true);
    message.setUpdatedAt(LocalDateTime.now());

    return messageRepository.save(message);
  }

  private void validateMessageOwnership(UUID userId, Message message) {
    if (!userId.equals(message.getUser().getId())) {
      throw new BaseException(MessageErrorCode.PERMISSION_MESSAGE_OWNER_DENIED);
    }
  }
}
