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
}
