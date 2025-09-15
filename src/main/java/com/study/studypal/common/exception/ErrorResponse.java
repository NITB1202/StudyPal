package com.study.studypal.common.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int statusCode;
  private String errorCode;
  private String message;
  private String timestamp;

  public ErrorResponse(int statusCode, String errorCode, String message) {
    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.message = message;
    this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
  }
}
