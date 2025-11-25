package com.study.studypal.team.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeamConstant {
  public static final int TEAM_CODE_LENGTH = 8;
  public static final String TEAM_AVATAR_FOLDER = "teams";
  public static final long MAX_OWNED_TEAMS = 5;
}
