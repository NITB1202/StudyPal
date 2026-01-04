package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_001", "Account not found."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "AUTH_002", "Email is already registered."),
  EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_003", "Email is not registered."),
  AUTH_METHOD_MISMATCH(
      HttpStatus.BAD_REQUEST,
      "AUTH_004",
      "This account was created through a third-party login. Please sign in using your linked provider."),
  INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_005", "Incorrect password."),
  INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_006", "Invalid or expired access token."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_007", "Invalid refresh token."),
  ACCOUNT_LOGGED_IN_ANOTHER_DEVICE(
      HttpStatus.UNAUTHORIZED, "AUTH_008", "Account has been logged in from another device."),
  REGISTRATION_INFO_NOT_FOUND(
      HttpStatus.NOT_FOUND, "AUTH_009", "Registration information not found in cache."),
  OAUTH_USER_INFO_NOT_FOUND(
      HttpStatus.BAD_GATEWAY, "AUTH_010", "Cannot retrieve user info from provider."),
  TOKEN_CACHE_UNAVAILABLE(
      HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_011", "Token cache is temporarily unavailable."),
  ;

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  AuthErrorCode(final HttpStatus httpStatus, final String code, final String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}
