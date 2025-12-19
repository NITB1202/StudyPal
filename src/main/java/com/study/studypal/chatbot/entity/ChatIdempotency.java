package com.study.studypal.chatbot.entity;

import com.study.studypal.chatbot.enums.TransactionStatus;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "chat_idempotency",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_chat_idempotency_user_key",
          columnNames = {"user_id", "idempotency_key"})
    })
public class ChatIdempotency {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "idempotency_key", nullable = false, length = 128)
  private String idempotencyKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_status", nullable = false)
  private TransactionStatus transactionStatus;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "response_message_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ChatMessage responseMessage;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
