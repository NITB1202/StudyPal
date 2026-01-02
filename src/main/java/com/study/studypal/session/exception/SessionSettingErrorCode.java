package com.study.studypal.session.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SessionSettingErrorCode implements ErrorCode {
  SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "SESSION_SETTING_001", "Session settings not found."),
  INVALID_TOTAL_TIME(
      HttpStatus.BAD_REQUEST,
      "SESSION_SETTING_002",
      "Total time must be greater than or equal to focus time plus break time"),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  SessionSettingErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
