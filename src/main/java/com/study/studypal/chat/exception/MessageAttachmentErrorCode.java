package com.study.studypal.chat.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MessageAttachmentErrorCode implements ErrorCode {
  ATTACHMENT_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST, "CHAT_ATTACH_001", "Attachment size exceeds the allowed limit."),
  ATTACHMENT_TOTAL_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST,
      "CHAT_ATTACH_002",
      "Total attachment size exceeds the allowed limit."),
  ;
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  MessageAttachmentErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
