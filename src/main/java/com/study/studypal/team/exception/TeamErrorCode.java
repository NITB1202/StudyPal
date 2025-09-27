package com.study.studypal.team.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TeamErrorCode implements ErrorCode {
  TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM_001", "Team not found."),
  INVALID_TEAM_CODE(HttpStatus.NOT_FOUND, "TEAM_002", "Team code is incorrect."),
  DUPLICATE_TEAM_NAME(
      HttpStatus.CONFLICT, "TEAM_003", "You have already created a team with the same name."),
  TEAM_NAME_UNCHANGED(
      HttpStatus.BAD_REQUEST, "TEAM_004", "The new team name is the same as the current name."),
  TEAM_OWNER_LIMIT_REACHED(
      HttpStatus.BAD_REQUEST, "TEAM_005", "You already own the maximum number of teams allowed.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  TeamErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
