package com.study.studypal.team.entity;

import com.study.studypal.user.entity.User;
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
@Table(
        name = "invitations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"invitee_id", "team_id"})
        },
        indexes = {
                @Index(name = "idx_invitations_invitee_invited_at", columnList = "invitee_id, invited_at DESC")
        }
)
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;
}
