package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskRecurrenceRuleErrorCode implements ErrorCode {
  INVALID_END_DATE(
      HttpStatus.BAD_REQUEST,
      "RECUR_001",
      "Recurrence end date must be after the plan's due date.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskRecurrenceRuleErrorCode(
      final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
