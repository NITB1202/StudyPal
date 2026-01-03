package com.study.studypal.chat.dto.response;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageUserResponseDto {
  private UUID id;

  private String name;

  private String avatarUrl;
}
