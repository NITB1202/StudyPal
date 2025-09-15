package com.study.studypal.user.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummaryResponseDto {
  private UUID id;

  private String name;

  private String avatarUrl;
}
