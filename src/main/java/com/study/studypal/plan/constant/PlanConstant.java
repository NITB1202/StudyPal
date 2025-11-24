package com.study.studypal.plan.constant;

import static com.study.studypal.common.util.Constants.DATE_PATTERN;

import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlanConstant {
  public static final String PLAN_CODE_PREFIX = "PLN-";
  public static final String TASK_CODE_PREFIX = "TSK-";
  public static final String CODE_NUMBER_FORMAT = "%05d";
  public static final DateTimeFormatter JSON_DATETIME_FORMATTER =
      DateTimeFormatter.ofPattern(DATE_PATTERN);
}
