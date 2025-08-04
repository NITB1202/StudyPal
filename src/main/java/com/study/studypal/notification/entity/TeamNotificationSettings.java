package com.study.studypal.notification.entity;

import com.study.studypal.team.entity.TeamUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TeamUser membership;

    @Column(name = "team_notification", nullable = false)
    private Boolean teamNotification;

    @Column(name = "team_plan_reminder", nullable = false)
    private Boolean teamPlanReminder;

    @Column(name = "chat_notification", nullable = false)
    private Boolean chatNotification;
}
