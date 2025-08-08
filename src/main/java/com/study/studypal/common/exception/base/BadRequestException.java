package com.study.studypal.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends BaseException {
    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}