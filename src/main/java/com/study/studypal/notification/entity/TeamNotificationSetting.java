package com.study.studypal.notification.entity;

import com.study.studypal.team.entity.TeamUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "team_notification_settings")
public class TeamNotificationSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "membership_id", nullable = false, unique = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private TeamUser membership;

  @Column(name = "team_notification", nullable = false)
  private Boolean teamNotification;

  @Column(name = "team_plan_reminder", nullable = false)
  private Boolean teamPlanReminder;

  @Column(name = "chat_notification", nullable = false)
  private Boolean chatNotification;
}
