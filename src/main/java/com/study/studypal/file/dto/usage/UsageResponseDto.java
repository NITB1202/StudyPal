package com.study.studypal.file.dto.usage;

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
public class UsageResponseDto {
  private Long usageUsed;

  private Long usageLimit;
}
