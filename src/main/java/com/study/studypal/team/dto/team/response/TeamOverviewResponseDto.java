package com.study.studypal.team.dto.team.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private int totalMembers;

    private Boolean isCreator;
}
