package com.study.studypal.plan.util;

import com.study.studypal.common.util.CursorUtils;
import com.study.studypal.plan.dto.statistic.response.TaskStatisticsResponseDto;
import com.study.studypal.plan.dto.task.internal.TaskStatisticsCursor;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskStatisticsCursorUtils {
  public static String encodeCursor(TaskStatisticsResponseDto statistic) {
    return CursorUtils.encode(statistic.getCompletedTaskCount(), statistic.getUserId());
  }

  public static TaskStatisticsCursor decodeCursor(String encodedCursor) {
    List<String> parts = CursorUtils.decode(encodedCursor);
    return new TaskStatisticsCursor(Long.parseLong(parts.get(0)), UUID.fromString(parts.get(1)));
  }
}
