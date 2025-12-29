package com.study.studypal.file.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserFileErrorCode implements ErrorCode {
  UNSUPPORTED_FILE_TYPE(
      HttpStatus.BAD_REQUEST,
      "USER_FILE_001",
      "Unsupported file type. Allowed types: documents, image."),
  FILE_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST, "USER_FILE_002", "File size exceeds the maximum allowed limit."),
  ;
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  UserFileErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
