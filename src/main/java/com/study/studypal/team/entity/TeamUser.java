package com.study.studypal.team.entity;

import com.study.studypal.team.enums.TeamRole;
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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "teams_users",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "team_id"})},
    indexes = {
      @Index(name = "idx_team_users_user_joined_at", columnList = "user_id, joined_at"),
      @Index(name = "idx_team_users_team_role_user", columnList = "team_id, role, user_id")
    })
public class TeamUser {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Column(name = "joined_at", nullable = false)
  private LocalDateTime joinedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private TeamRole role;

  @Formula(
      """
        CASE role
            WHEN 'CREATOR' THEN 1
            WHEN 'ADMIN' THEN 2
            WHEN 'MEMBER' THEN 3
        END
    """)
  private int rolePriority;
}
