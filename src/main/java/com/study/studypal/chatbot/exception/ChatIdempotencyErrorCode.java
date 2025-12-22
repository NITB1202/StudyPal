package com.study.studypal.chatbot.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatIdempotencyErrorCode implements ErrorCode {
  CHAT_IDEMPOTENCY_NOT_FOUND(
      HttpStatus.NOT_FOUND, "CHAT_IDEMPOTENCY_001", "Chat idempotency record not found."),
  CHAT_IDEMPOTENCY_RESPONSE_NOT_AVAILABLE(
      HttpStatus.CONFLICT,
      "CHAT_IDEMPOTENCY_002",
      "Chat response only available when status is DONE."),
  CHAT_IDEMPOTENCY_REQUEST_IN_PROGRESS(
      HttpStatus.CONFLICT,
      "CHAT_IDEMPOTENCY_003",
      "Request with the same idempotency key is already being processed.");
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ChatIdempotencyErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
