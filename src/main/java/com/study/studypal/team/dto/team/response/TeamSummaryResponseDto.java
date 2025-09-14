package com.study.studypal.team.dto.team.response;

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
public class TeamSummaryResponseDto {
  private UUID id;

  private String name;

  private String avatarUrl;

  private boolean managedByUser;
}
