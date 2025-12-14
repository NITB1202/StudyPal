package com.study.studypal.chatbot.dto.internal;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanContext {
  private String title;

  private String description;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;

  private Float progress;

  private List<TaskContext> tasks;
}
