package com.study.studypal.file.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UsageErrorCode implements ErrorCode {
  USAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USAGE_001", "Storage usage not found."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  UsageErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
