package com.study.studypal.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class UnauthorizedException extends BaseException {
    public UnauthorizedException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }
}
