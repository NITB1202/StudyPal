package com.study.studypal.notification.entity;

import com.study.studypal.notification.enums.Platform;
import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
    name = "device_tokens",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "token"})},
    indexes = {@Index(name = "idx_device_tokens_user_token", columnList = "user_id, token")})
public class DeviceToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "platform", nullable = false)
  @Enumerated(EnumType.STRING)
  private Platform platform;

  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdated;
}
