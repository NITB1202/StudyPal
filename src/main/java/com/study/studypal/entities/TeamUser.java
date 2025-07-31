package com.study.studypal.entities;

import com.study.studypal.enums.TeamRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "teams_users")
@IdClass(TeamUserId.class)
public class TeamUser {
    @Id
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeamRole role;
}

