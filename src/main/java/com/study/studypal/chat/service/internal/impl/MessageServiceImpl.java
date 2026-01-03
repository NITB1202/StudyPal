package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.dto.request.MarkMessagesAsReadRequestDto;
import com.study.studypal.chat.dto.request.SendMessageRequestDto;
import com.study.studypal.chat.dto.request.UpdateMessageRequestDto;
import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.repository.MessageRepository;
import com.study.studypal.chat.service.internal.MessageService;
import com.study.studypal.team.entity.Team;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
  public Message updateMessage(UUID userId, UUID messageId, UpdateMessageRequestDto request) {
    return null;
  }

  @Override
  public void markMessagesAsRead(UUID userId, UUID teamId, MarkMessagesAsReadRequestDto request) {}

  @Override
  public void deleteMessage(UUID userId, UUID messageId) {}
}
