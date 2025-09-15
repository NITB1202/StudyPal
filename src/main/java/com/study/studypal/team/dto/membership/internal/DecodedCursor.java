package com.study.studypal.team.dto.membership.internal;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DecodedCursor {
  private int rolePriority;

  private String name;

  private UUID userId;
}
