package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskCounterErrorCode implements ErrorCode {
  TASK_COUNTER_ERROR_CODE(HttpStatus.NOT_FOUND, "COUNTER_001", "Task counter not found.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskCounterErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
