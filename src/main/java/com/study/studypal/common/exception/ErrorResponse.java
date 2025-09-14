package com.study.studypal.common.exception;

import java.time.LocalTime;
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
    this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }
}
