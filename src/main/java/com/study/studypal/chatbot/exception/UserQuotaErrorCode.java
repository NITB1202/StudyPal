package com.study.studypal.chatbot.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserQuotaErrorCode implements ErrorCode {
  USER_QUOTA_NOT_FOUND(HttpStatus.NOT_FOUND, "QUOTA_001", "User quota not found."),
  INSUFFICIENT_QUOTA(
      HttpStatus.BAD_REQUEST,
      "QUOTA_002",
      "User has insufficient quota to perform this operation."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  UserQuotaErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
