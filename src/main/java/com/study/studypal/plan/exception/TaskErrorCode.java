package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements ErrorCode {
  INVALID_DUE_DATE(
      HttpStatus.BAD_REQUEST, "TASK_001", "Task '%s' must have a due date after its start date."),
  TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK_002", "Task not found.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
