package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskReminderErrorCode implements ErrorCode {
  REMINDER_ALREADY_EXISTS(
      HttpStatus.CONFLICT, "REMINDER_001", "Reminder %s is already exists in the plan."),
  INVALID_REMINDER(
      HttpStatus.BAD_REQUEST,
      "REMINDER_002",
      "Reminder %s must be within the plan's start and due dates."),
  SCHEDULE_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "REMINDER_003", "Failed to schedule reminder %s."),
  CANCEL_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "REMINDER_004", "Failed to cancel reminder with id %s");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskReminderErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
