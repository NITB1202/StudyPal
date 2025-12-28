package com.study.studypal.file.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FolderErrorCode implements ErrorCode {
  FOLDER_ALREADY_EXISTS(HttpStatus.CONFLICT, "FOLDER_001", "Folder already exists."),
  FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLDER_002", "Folder not found."),
  PERMISSION_FOLDER_OWNER_DENIED(
      HttpStatus.FORBIDDEN, "FOLDER_003", "You are not the owner of this folder."),
  FOLDER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "FOLDER_004", "Folder is already deleted."),
  ;
  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  FolderErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
