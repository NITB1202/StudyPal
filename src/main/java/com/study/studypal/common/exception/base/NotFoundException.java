package com.study.studypal.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends BaseException {
    public NotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }
}
