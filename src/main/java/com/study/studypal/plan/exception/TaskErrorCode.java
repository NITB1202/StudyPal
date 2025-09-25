package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements ErrorCode {
  TASK_ALREADY_EXISTS(HttpStatus.CONFLICT, "TASK_001", "Task '%s' is already exists in the plan."),
  INVALID_DUE_DATE(
      HttpStatus.BAD_REQUEST,
      "TASK_002",
      "Task '%s' must have a due date within the plan's start and end dates.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
