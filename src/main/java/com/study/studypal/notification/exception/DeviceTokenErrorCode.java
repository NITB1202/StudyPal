package com.study.studypal.notification.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DeviceTokenErrorCode implements ErrorCode {
  DEVICE_TOKEN_NOT_FOUND(
      HttpStatus.NOT_FOUND, "DEVICE_001", "The device token is not associated with this user."),
  PUSH_SERVICE_INIT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "DEVICE_002",
      "Failed to initialize push notification service: %s");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  DeviceTokenErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
