package com.study.studypal.team.dto.Team.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamOverviewResponseDto {
    private UUID id;

    private String name;

    private String avatarUrl;

    private String description;

    private String teamCode;
}
