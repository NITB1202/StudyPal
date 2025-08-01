package com.study.studypal.team.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "inviter_name", nullable = false)
    private String inviterName;

    @Column(name = "inviter_avatar_url", nullable = false)
    private String inviterAvatarUrl;

    @Column(name = "invitee_id", nullable = false)
    private UUID inviteeId;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;
}
