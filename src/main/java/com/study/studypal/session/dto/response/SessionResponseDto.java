package com.study.studypal.session.dto.response;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionResponseDto {
  private UUID id;

  private Long elapsedTimeInSeconds;

  private Long durationInSeconds;
}
