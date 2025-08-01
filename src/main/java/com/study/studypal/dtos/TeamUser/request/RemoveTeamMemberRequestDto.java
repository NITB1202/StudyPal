package com.study.studypal.dtos.TeamUser.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemoveTeamMemberRequestDto {
    @NotNull(message = "Team id is required.")
    private UUID teamId;

    @NotNull(message = "Member id is required.")
    private UUID memberId;
}
