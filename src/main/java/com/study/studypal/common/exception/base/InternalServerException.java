package com.study.studypal.common.exception.base;

import org.springframework.http.HttpStatus;

public abstract class InternalServerException extends BaseException {
    public InternalServerException(String errorCode, String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message);
    }
}
