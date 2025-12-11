package com.study.studypal.chatbot.entity;

import com.study.studypal.chatbot.enums.ContextType;
import com.study.studypal.chatbot.enums.Sender;
import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_messages")
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @JoinColumn(name = "id")
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "sender", nullable = false)
  private Sender sender;

  @Column(name = "message", nullable = false)
  private String message;

  @Column(name = "context_id")
  private UUID contextId;

  @Enumerated(EnumType.STRING)
  @Column(name = "context_type")
  private ContextType contextType;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
