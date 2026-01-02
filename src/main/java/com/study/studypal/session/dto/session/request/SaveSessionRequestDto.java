package com.study.studypal.session.dto.session.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveSessionRequestDto {
  @NotNull(message = "Study time is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime studiedAt;

  @NotNull(message = "Duration is required")
  @Positive(message = "Duration must be positive")
  private Long durationInSeconds;

  @NotNull(message = "Elapsed time is required")
  @PositiveOrZero(message = "Elapsed time must be zero or positive")
  private Long elapsedTimeInSeconds;
}
