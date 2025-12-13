package com.study.studypal.chatbot.repository;

import com.study.studypal.chatbot.entity.ChatMessageAttachment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageAttachmentRepository
    extends JpaRepository<ChatMessageAttachment, UUID> {
  @Query("SELECT a FROM ChatMessageAttachment a WHERE a.chatMessage.id = :messageId")
  List<ChatMessageAttachment> findByMessageId(@Param("messageId") UUID messageId);
}
