package com.study.studypal.plan.dto.plan.response;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePlanResponseDto {
  private UUID id;

  private String title;

  private String description;
}
