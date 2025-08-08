package com.study.studypal.common.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus statusCode;
    private final String errorCode;

    public BaseException(final HttpStatus statusCode, final String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
