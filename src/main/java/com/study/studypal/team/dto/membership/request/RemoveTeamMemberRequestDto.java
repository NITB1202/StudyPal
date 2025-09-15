package com.study.studypal.team.dto.membership.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
