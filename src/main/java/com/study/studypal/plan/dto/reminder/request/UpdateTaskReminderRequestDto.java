package com.study.studypal.plan.dto.reminder.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskReminderRequestDto {
  @NotNull(message = "Remind at is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime remindAt;
}
