package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.UnauthorizedException;

public class InvalidRefreshTokenException extends UnauthorizedException {
    public InvalidRefreshTokenException() {
        super("INVALID_REFRESH_TOKEN", "Invalid refresh token.");
    }
}
