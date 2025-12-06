package com.study.studypal.plan.util;

import com.study.studypal.common.exception.BaseException;
import com.study.studypal.plan.dto.task.internal.TaskCursor;
import com.study.studypal.plan.entity.Task;
import com.study.studypal.plan.exception.TaskErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCursorUtils {

  public static String encodeCursor(Task task) {
    return Base64.getEncoder()
        .encodeToString(
            (task.getDueDate() + "|" + task.getPriorityValue() + "|" + task.getId())
                .getBytes(StandardCharsets.UTF_8));
  }

  public static TaskCursor decodeCursor(String encodedCursor) {
    try {
      String decoded =
          new String(Base64.getDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
      String[] parts = decoded.split("\\|", 3);
      return new TaskCursor(
          LocalDateTime.parse(parts[0]), Integer.parseInt(parts[1]), UUID.fromString(parts[2]));
    } catch (Exception e) {
      throw new BaseException(TaskErrorCode.CURSOR_DECODE_FAILED, e.getMessage());
    }
  }
}
