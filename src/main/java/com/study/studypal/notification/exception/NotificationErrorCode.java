package com.study.studypal.notification.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements ErrorCode {
  NOTIFICATION_DEFINITION_NOT_FOUND(
      HttpStatus.INTERNAL_SERVER_ERROR, "NOTI_001", "Notification definition not found: %s"),
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
