package com.study.studypal.plan.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TaskErrorCode implements ErrorCode {
  TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK_001", "Task not found."),
  PERMISSION_TASK_OWNER_DENIED(
      HttpStatus.FORBIDDEN, "TASK_002", "You are not the owner of this task."),
  PERSONAL_TASK_REQUIRED(
      HttpStatus.BAD_REQUEST, "TASK_003", "This operation is only allowed for personal tasks."),
  TEAM_TASK_REQUIRED(
      HttpStatus.FORBIDDEN, "TASK_004", "This operation is only allowed for team tasks."),
  TASK_ALREADY_COMPLETED(HttpStatus.CONFLICT, "TASK_005", "Task is already completed."),
  TASK_ASSIGNEE_ONLY(
      HttpStatus.FORBIDDEN,
      "TASK_006",
      "Only the task assignee is allowed to perform this action."),
  TASK_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "TASK_007", "Task is already deleted."),
  TASK_NOT_DELETED(HttpStatus.BAD_REQUEST, "TASK_008", "Task is not deleted."),
  TASK_SCOPE_REQUIRED(
      HttpStatus.BAD_REQUEST, "TASK_009", "Apply scope must be specified for cloned tasks."),
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
