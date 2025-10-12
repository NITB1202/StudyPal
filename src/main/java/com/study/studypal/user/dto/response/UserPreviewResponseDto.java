package com.study.studypal.user.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPreviewResponseDto {
  private UUID id;

  private String name;

  private String email;

  private String avatarUrl;
}
