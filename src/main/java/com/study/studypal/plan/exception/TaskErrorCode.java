package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements ErrorCode {
  INVALID_DUE_DATE(
      HttpStatus.BAD_REQUEST, "TASK_001", "Task '%s' must have a due date after its start date."),
  TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK_002", "Task not found."),
  PERMISSION_TASK_OWNER_DENIED(
      HttpStatus.FORBIDDEN, "TASK_003", "You are not the owner of this task."),
  PERSONAL_TASK_REQUIRED(
      HttpStatus.BAD_REQUEST, "TASK_004", "This operation is only allowed for personal tasks."),
  BLANK_TASK(HttpStatus.BAD_REQUEST, "TASK_005", "The task content can't be left blank."),
  TEAM_TASK_REQUIRED(
      HttpStatus.FORBIDDEN, "TASK_006", "This operation is only allowed for team tasks."),
  TASK_ALREADY_COMPLETED(HttpStatus.CONFLICT, "TASK_007", "Task is already completed."),
  TASK_ASSIGNEE_ONLY(
      HttpStatus.FORBIDDEN,
      "TASK_008",
      "Only the task assignee is allowed to perform this action."),
  TASK_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "TASK_009", "Task is already deleted."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TaskErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
