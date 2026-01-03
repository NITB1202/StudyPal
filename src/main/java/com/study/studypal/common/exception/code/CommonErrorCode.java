package com.study.studypal.common.exception.code;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
  INVALID_M0NTH(HttpStatus.BAD_REQUEST, "COMMON_001", "Month must be between 1 and 12."),
  INVALID_YEAR(HttpStatus.BAD_REQUEST, "COMMON_002", "Year must be greater than 0."),
  CURSOR_ENCODE_FAILED(HttpStatus.BAD_REQUEST, "COMMON_004", "Cursor parts must not be empty."),
  CURSOR_DECODE_FAILED(HttpStatus.BAD_REQUEST, "COMMON_005", "Failed to decode cursor: %s"),
  FIELD_BLANK(HttpStatus.BAD_REQUEST, "COMMON_006", "%s is blank."),
  FIELD_NULL(HttpStatus.BAD_REQUEST, "COMMON_007", "%s is null."),
  INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "COMMON_008", "Invalid time range."),
  JSON_SERIALIZE_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_009", "Failed to serialize object."),
  JSON_DESERIALIZE_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_010", "Failed to deserialize JSON string."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  CommonErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
