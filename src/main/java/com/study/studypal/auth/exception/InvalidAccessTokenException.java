package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.UnauthorizedException;

public class InvalidAccessTokenException extends UnauthorizedException {
    public InvalidAccessTokenException() {
        super("INVALID_ACCESS_TOKEN", "Invalid access token.");
    }
}
