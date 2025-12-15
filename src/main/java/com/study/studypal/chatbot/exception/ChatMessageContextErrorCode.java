package com.study.studypal.chatbot.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatMessageContextErrorCode implements ErrorCode {
  CONTEXT_TYPE_REQUIRED(
      HttpStatus.BAD_REQUEST,
      "CONTEXT_001",
      "Context type is required when context id is provided."),
  SERIALIZE_CONTEXT_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "CONTEXT_002", "Failed to serialize context data."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ChatMessageContextErrorCode(
      final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
