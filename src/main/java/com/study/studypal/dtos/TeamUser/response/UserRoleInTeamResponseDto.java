package com.study.studypal.dtos.TeamUser.response;

import com.study.studypal.enums.TeamRole;
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