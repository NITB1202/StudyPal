package com.study.studypal.chatbot.dto.external;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponseDto {
  private String reply;

  private Long inputTokens;

  private Long outputTokens;

  private PlanOutput generatedPlan;

  private List<TaskOutput> generatedTasksJson;
}
