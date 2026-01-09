package com.study.studypal.plan.constant;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlanConstant {
  public static final String PLAN_CODE_PREFIX = "PLN-";
  public static final String TASK_CODE_PREFIX = "TSK-";
  public static final String CODE_NUMBER_FORMAT = "%05d";

  public static final LocalDateTime SAFE_MIN_DATE_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
  public static final LocalDateTime SAFE_MAX_DATE_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
}
