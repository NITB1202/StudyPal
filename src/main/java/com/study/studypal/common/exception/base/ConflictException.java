package com.study.studypal.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class ConflictException extends BaseException {
    public ConflictException(String errorCode, String message) {
        super(HttpStatus.CONFLICT, errorCode, message);
    }
}