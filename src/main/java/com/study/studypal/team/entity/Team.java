package com.study.studypal.team.entity;

import com.study.studypal.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name= "team_code", nullable = false, unique = true)
    private String teamCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "total_members", nullable = false)
    private Integer totalMembers;

    @Column(name = "description")
    private String description;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TeamUser> members;
}
