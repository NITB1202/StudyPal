package com.study.studypal.team.dto.membership.response;

import java.util.List;
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
public class ListTeamMemberResponseDto {
  private List<TeamMemberResponseDto> members;

  private long total;

  private String nextCursor;
}
