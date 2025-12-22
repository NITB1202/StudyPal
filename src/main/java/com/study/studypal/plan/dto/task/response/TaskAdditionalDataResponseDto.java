package com.study.studypal.plan.dto.task.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.team.enums.TeamRole;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskAdditionalDataResponseDto {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private TeamRole role;

  private UUID planId;

  private String planCode;

  private UUID assigneeId;

  private String assigneeAvatarUrl;

  private String assigneeName;
}
