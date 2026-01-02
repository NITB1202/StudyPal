package com.study.studypal.session.dto.setting.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionSettingResponseDto {
  private Long focusTimeInSeconds;

  private Long breakTimeInSeconds;

  private Long totalTimeInSeconds;

  private Boolean enableBgMusic;
}
