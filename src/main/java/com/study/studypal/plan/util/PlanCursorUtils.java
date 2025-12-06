package com.study.studypal.plan.util;

import com.study.studypal.common.util.CursorUtils;
import com.study.studypal.plan.dto.plan.internal.PlanCursor;
import com.study.studypal.plan.entity.Plan;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanCursorUtils {

  public static String encodeCursor(Plan plan) {
    return CursorUtils.encode(plan.getDueDate(), plan.getId());
  }

  public static PlanCursor decodeCursor(String encodedCursor) {
    List<String> parts = CursorUtils.decode(encodedCursor);
    return new PlanCursor(LocalDateTime.parse(parts.get(0)), UUID.fromString(parts.get(1)));
  }
}
