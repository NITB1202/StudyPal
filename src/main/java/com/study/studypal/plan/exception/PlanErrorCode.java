package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PlanErrorCode implements ErrorCode {
  PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN_001", "Plan not found."),
  PLAN_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "PLAN_002", "Plan is already deleted."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  PlanErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
