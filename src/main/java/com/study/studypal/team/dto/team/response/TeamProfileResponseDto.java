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
public class TeamProfileResponseDto {
  private UUID id;

  private String avatarUrl;

  private String name;

  private String description;

  private String creatorName;

  private String creatorAvatarUrl;

  private long totalMembers;
}
