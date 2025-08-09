package com.study.studypal.team.dto.membership.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.study.studypal.team.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMemberRoleRequestDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "Member id is required")
    private UUID memberId;

    @NotNull(message = "Role is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TeamRole role;
}
