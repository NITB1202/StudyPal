package com.study.studypal.notification.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements ErrorCode {
  PERMISSION_UPDATE_NOTIFICATION_DENIED(
      HttpStatus.BAD_REQUEST,
      "N0TI_001",
      "Some notifications are already read or do not belong to your account."),
  PERMISSION_DELETE_NOTIFICATION_DENIED(
      HttpStatus.BAD_REQUEST,
      "N0TI_002",
      "Some notifications are already deleted or do not belong to your account."),
  NOTIFICATION_DEFINITION_NOT_FOUND(
      HttpStatus.INTERNAL_SERVER_ERROR, "NOTI_003", "Notification definition not found: %s"),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  NotificationErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
