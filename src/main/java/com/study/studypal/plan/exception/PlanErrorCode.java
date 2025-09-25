package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PlanErrorCode implements ErrorCode {
  START_DATE_AFTER_DUE_DATE(
      HttpStatus.BAD_REQUEST, "PLAN_001", "Start date must be before the due date.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  PlanErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
