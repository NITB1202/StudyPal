package com.study.studypal.chatbot.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.dto.plan.request.CreatePlanRequestDto;
import com.study.studypal.plan.dto.task.request.CreateTaskRequestDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponseDto {
  private UUID id;

  private String reply;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  private CreatePlanRequestDto generatedPlan;

  private List<CreateTaskRequestDto> generatedTasks;
}
