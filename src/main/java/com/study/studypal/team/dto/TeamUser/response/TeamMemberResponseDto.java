package com.study.studypal.team.dto.TeamUser.response;

import com.study.studypal.team.enums.TeamRole;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMemberResponseDto {
    private UUID userId;

    private String name;

    private String avatarUrl;

    private TeamRole role;
}
