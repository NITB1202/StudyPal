package com.study.studypal.dtos.Team.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamProfileResponseDto {
    private UUID id;

    private String avatarUrl;

    private String name;

    private String description;

    private String creatorName;

    private String creatorAvatarUrl;

    private long totalMembers;
}
