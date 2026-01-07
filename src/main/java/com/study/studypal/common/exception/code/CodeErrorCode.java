package com.study.studypal.common.exception.code;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CodeErrorCode implements ErrorCode {
  GENERATE_QR_CODE_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "CODE_001", "Generate QR code failed: %s"),
  QR_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "CODE_002", "QR code not found."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  CodeErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
