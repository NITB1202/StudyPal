package com.study.studypal.team.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "team_notification_settings")
public class TeamNotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "team_notification", nullable = false)
    private Boolean teamNotification;

    @Column(name = "team_plan_reminder", nullable = false)
    private Boolean teamPlanReminder;

    @Column(name = "chat_notification", nullable = false)
    private Boolean chatNotification;
}
