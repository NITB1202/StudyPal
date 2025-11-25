package com.study.studypal.common.exception.code;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DateErrorCode implements ErrorCode {
  INVALID_M0NTH(HttpStatus.BAD_REQUEST, "DATE_001", "Month must be between 1 and 12."),
  INVALID_YEAR(HttpStatus.BAD_REQUEST, "DATE_002", "Year must be greater than 0.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  DateErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
