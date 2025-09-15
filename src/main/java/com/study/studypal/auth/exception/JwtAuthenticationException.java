package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
  public JwtAuthenticationException(ErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
