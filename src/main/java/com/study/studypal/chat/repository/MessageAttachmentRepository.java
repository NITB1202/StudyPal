package com.study.studypal.chat.repository;

import com.study.studypal.chat.entity.MessageAttachment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachment, UUID> {
  List<MessageAttachment> findAllByMessageId(UUID messageId);
}
