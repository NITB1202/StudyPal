package com.study.studypal.team.dto.membership.request;

import jakarta.validation.constraints.NotNull;
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
public class RemoveTeamMemberRequestDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "Member id is required")
    private UUID memberId;
}
