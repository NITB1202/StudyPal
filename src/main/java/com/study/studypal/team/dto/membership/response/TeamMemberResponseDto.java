package com.study.studypal.team.dto.membership.response;

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
public class TeamMemberResponseDto {
  private UUID userId;

  private String name;

  private String avatarUrl;

  private TeamRole role;
}
