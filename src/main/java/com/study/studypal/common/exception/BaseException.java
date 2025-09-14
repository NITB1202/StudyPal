package com.study.studypal.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
  private final transient ErrorCode errorCode;

  public BaseException(ErrorCode errorCode, Object... args) {
    super(String.format(errorCode.getMessage(), args));
    this.errorCode = errorCode;
  }
}
