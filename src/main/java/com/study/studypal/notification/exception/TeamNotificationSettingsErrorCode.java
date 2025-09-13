package com.study.studypal.notification.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TeamNotificationSettingsErrorCode implements ErrorCode {
    SETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, "SET_001", "Team notification settings not found.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TeamNotificationSettingsErrorCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
