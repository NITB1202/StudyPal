package com.study.studypal.chatbot.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatbotErrorCode implements ErrorCode {
  CHATBOT_SERVICE_UNAVAILABLE(
      HttpStatus.SERVICE_UNAVAILABLE,
      "CHATBOT_001",
      "Chatbot service is unavailable. Please try again later."),
  CHATBOT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "CHATBOT_002", "Invalid chatbot request."),
  ;
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ChatbotErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
