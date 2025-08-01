package com.study.studypal.entities;

import com.study.studypal.enums.TeamRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "teams_users")
public class TeamUser {
    @EmbeddedId
    private TeamUserId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeamRole role;

    @Formula("""
        CASE role
            WHEN 'CREATOR' THEN 1
            WHEN 'ADMIN' THEN 2
            WHEN 'MEMBER' THEN 3
        END
    """)
    private int rolePriority;
}