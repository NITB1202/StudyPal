package com.study.studypal.plan.dto.task.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskAdditionalDataResponseDto {
  private UUID planId;

  private String planCode;

  private UUID assigneeId;

  private String assigneeAvatarUrl;

  private String assigneeName;
}
