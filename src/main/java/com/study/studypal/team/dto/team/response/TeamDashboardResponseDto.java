package com.study.studypal.team.dto.team.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.team.enums.TeamRole;
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
public class TeamDashboardResponseDto {
  private UUID id;

  private String name;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private TeamRole role;

  private String avatarUrl;

  private String description;

  private int totalMembers;
}
