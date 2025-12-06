package com.study.studypal.plan.util;

import com.study.studypal.common.util.CursorUtils;
import com.study.studypal.plan.dto.task.internal.TaskCursor;
import com.study.studypal.plan.entity.Task;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCursorUtils {

  public static String encodeCursor(Task task) {
    return CursorUtils.encode(task.getDueDate(), task.getPriorityValue(), task.getId());
  }

  public static TaskCursor decodeCursor(String encodedCursor) {
    List<String> parts = CursorUtils.decode(encodedCursor);
    return new TaskCursor(
        LocalDateTime.parse(parts.get(0)),
        Integer.parseInt(parts.get(1)),
        UUID.fromString(parts.get(2)));
  }
}
