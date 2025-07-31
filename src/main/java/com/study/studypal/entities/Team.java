package com.study.studypal.entities;

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
@Table(name = "team")
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

    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    @Column(name = "total_members", nullable = false)
    private Integer totalMembers;

    @Column(name = "description")
    private String description;

    @Column(name = "avatar_url")
    private String avatarUrl;
}
