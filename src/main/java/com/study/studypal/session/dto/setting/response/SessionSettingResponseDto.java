package com.study.studypal.session.dto.setting.response;

import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionSettingResponseDto {
  private UUID id;

  private LocalTime focusTime;

  private LocalTime breakTime;

  private LocalTime totalTime;

  private String bgMusicName;

  private String bgMusicUrl;
}
