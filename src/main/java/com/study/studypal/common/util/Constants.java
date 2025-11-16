package com.study.studypal.common.util;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
  // API constants
  public static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";
  public static final String DEFAULT_PAGE_SIZE = "10";

  // Authentication constants
  public static final String PASSWORD_RULE_MESSAGE =
      "Password must be at least 3 characters long and contain both letters and numbers.";

  // Team constants
  public static final long MAX_OWNED_TEAMS = 5;

  // Plan constants
  public static final String PLAN_CODE_PREFIX = "PLN-";
  public static final DateTimeFormatter JSON_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
