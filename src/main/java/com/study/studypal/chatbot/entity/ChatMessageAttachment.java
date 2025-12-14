package com.study.studypal.chatbot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "chat_message_attachments")
public class ChatMessageAttachment {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id", nullable = false)
  private ChatMessage chatMessage;

  @Column(name = "url", nullable = false)
  private String url;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "size", nullable = false)
  private Long size;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;
}
