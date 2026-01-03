package com.study.studypal.chat.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageErrorCode implements ErrorCode {
  MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE_001", "Message not found."),
  PERMISSION_MESSAGE_OWNER_DENIED(
      HttpStatus.FORBIDDEN, "MESSAGE_002", "You are not the owner of this message."),
  MESSAGE_EDIT_TIME_EXPIRED(
      HttpStatus.BAD_REQUEST, "MESSAGE_003", "Message can no longer be edited."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  MessageErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
