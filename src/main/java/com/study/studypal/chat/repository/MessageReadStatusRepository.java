package com.study.studypal.chat.repository;

import com.study.studypal.chat.entity.MessageReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, UUID> {
  List<MessageReadStatus> findAllByMessageId(UUID messageId);
}
