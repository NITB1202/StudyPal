package com.study.studypal.team.dto.TeamUser.response;

import com.study.studypal.team.enums.TeamRole;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleInTeamResponseDto {
    private UUID userId;

    private TeamRole role;
}