package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskReminderErrorCode implements ErrorCode {
  REMINDER_ALREADY_EXISTS(
      HttpStatus.CONFLICT, "REMINDER_001", "Reminder %s is already exists in the task."),
  INVALID_REMINDER(
      HttpStatus.BAD_REQUEST,
      "REMINDER_002",
      "Reminder %s must be within the task's start and due dates."),
  SCHEDULE_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "REMINDER_003", "Failed to schedule reminder %s."),
  CANCEL_REMINDER_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "REMINDER_004", "Failed to cancel reminder with id %s"),
  PAST_REMINDER_NOT_ALLOWED(
      HttpStatus.BAD_REQUEST, "REMINDER_005", "Reminder %s cannot be set in the past."),
  REMINDER_NOT_FOUND(HttpStatus.NOT_FOUND, "REMINDER_006", "Reminder not found."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskReminderErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
