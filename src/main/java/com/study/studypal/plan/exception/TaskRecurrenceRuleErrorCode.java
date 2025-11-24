package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskRecurrenceRuleErrorCode implements ErrorCode {
  INVALID_END_DATE(
      HttpStatus.BAD_REQUEST,
      "RECUR_001",
      "Recurrence end date must be after the task's start date."),
  INVALID_WEEKLY_RECURRENCE(
      HttpStatus.BAD_REQUEST, "RECUR_002", "Weekdays must not be empty for weekly recurrence."),
  RECURRING_TASK_DURATION_INVALID(
      HttpStatus.BAD_REQUEST, "RECUR_003", "Recurring tasks must start and end on the same day.");

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
