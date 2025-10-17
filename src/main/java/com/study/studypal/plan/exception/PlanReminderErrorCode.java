package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PlanReminderErrorCode implements ErrorCode {
  INVALID_REMINDER(
      HttpStatus.BAD_REQUEST,
      "REMINDER_001",
      "Each reminder must be within the plan's start and due dates."),
  SCHEDULE_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "REMINDER_002",
      "Failed to schedule reminder for plan with id: %s."),
  CANCEL_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "REMINDER_003", "Failed to cancel reminder with id: %s");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  PlanReminderErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
