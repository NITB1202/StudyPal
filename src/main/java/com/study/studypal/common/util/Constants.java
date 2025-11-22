package com.study.studypal.common.util;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  public static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";
  public static final String DEFAULT_PAGE_SIZE = "10";
  public static final DateTimeFormatter JSON_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  // System image folder
  public static final String USER_AVATAR_FOLDER = "users";
  public static final String TEAM_AVATAR_FOLDER = "teams";

  // Team constants
  public static final long MAX_OWNED_TEAMS = 5;

  // Plan constants
  public static final String PLAN_CODE_PREFIX = "PLN-";
  public static final String TASK_CODE_PREFIX = "TSK-";
  public static final String CODE_NUMBER_FORMAT = "%05d";
}
