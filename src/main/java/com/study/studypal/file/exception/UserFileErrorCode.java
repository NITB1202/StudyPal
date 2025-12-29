package com.study.studypal.file.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserFileErrorCode implements ErrorCode {
  UNSUPPORTED_FILE_TYPE(
      HttpStatus.BAD_REQUEST,
      "USER_FILE_001",
      "Unsupported file type. Allowed types: documents, image."),
  FILE_SIZE_EXCEEDED(
      HttpStatus.BAD_REQUEST, "USER_FILE_002", "File size exceeds the maximum allowed limit."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_FILE_003", "File not found."),
  FILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_FILE_004", "File already exists."),
  PERMISSION_FILE_OWNER_DENIED(
      HttpStatus.FORBIDDEN, "USER_FILE_005", "You are not the owner of this file."),
  PERMISSION_MOVE_FILE_DENIED(
      HttpStatus.FORBIDDEN, "USER_FILE_006", "Cannot move file to the target folder."),
  FILE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER_FILE_007", "File is already deleted."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  UserFileErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
