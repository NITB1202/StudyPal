package com.study.studypal.chatbot.dto.internal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.studypal.plan.enums.Priority;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskContext {
  private String content;

  private String assigneeName;

  private LocalDateTime startDate;

  private LocalDateTime dueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Priority priority;
}
