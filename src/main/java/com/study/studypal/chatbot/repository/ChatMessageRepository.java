package com.study.studypal.chatbot.repository;

import com.study.studypal.chatbot.entity.ChatMessage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {}
