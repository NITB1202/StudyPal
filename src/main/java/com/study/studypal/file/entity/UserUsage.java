package com.study.studypal.file.entity;

import com.study.studypal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "user_usages")
public class UserUsage {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  private User user;

  @Column(name = "usage_used", nullable = false)
  private Long usageUsed;

  @Column(name = "usage_limit", nullable = false)
  private Long usageLimit;
}
