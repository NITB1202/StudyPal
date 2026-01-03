package com.study.studypal.chat.service.internal.impl;

import com.study.studypal.chat.entity.Message;
import com.study.studypal.chat.entity.MessageReadStatus;
import com.study.studypal.chat.repository.MessageReadStatusRepository;
import com.study.studypal.chat.service.internal.MessageStatusService;
import com.study.studypal.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageStatusServiceImpl implements MessageStatusService {
  private final MessageReadStatusRepository statusRepository;
  @PersistenceContext private final EntityManager entityManager;

  @Override
  public List<MessageReadStatus> getByMessageId(UUID messageId) {
    return statusRepository.findAllByMessageId(messageId);
  }

  @Override
  public MessageReadStatus markMessageAsRead(UUID userId, Message message) {
    User user = entityManager.getReference(User.class, userId);
    MessageReadStatus status =
        MessageReadStatus.builder().message(message).user(user).readAt(LocalDateTime.now()).build();
    return statusRepository.save(status);
  }

  @Override
  public void markMessagesAsRead(UUID userId, List<Message> messages) {
    LocalDateTime now = LocalDateTime.now();
    User user = entityManager.getReference(User.class, userId);

    List<MessageReadStatus> readStatuses = new ArrayList<>();

    for (Message message : messages) {
      if (!statusRepository.existsByUserIdAndMessageId(userId, message.getId())) {
        MessageReadStatus status =
            MessageReadStatus.builder().message(message).user(user).readAt(now).build();
        readStatuses.add(status);
      } else {
        // The list is sorted desc
        // if this message is read then all the messages after this is read
        break;
      }
    }

    statusRepository.saveAll(readStatuses);
  }
}
