package com.study.studypal.session.dto.setting.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSessionSettingRequestDto {
  @NotNull(message = "Focus time is required")
  @Positive(message = "Focus time must be positive")
  private Long focusTimeInSeconds;

  @NotNull(message = "Break time is required")
  @Positive(message = "Break time must be positive")
  private Long breakTimeInSeconds;

  @NotNull(message = "Total time is required")
  @Positive(message = "Total time must be positive")
  private Long totalTimeInSeconds;

  @NotNull(message = "Enable BG music is required")
  private Boolean enableBgMusic;
}
