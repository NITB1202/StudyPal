package com.study.studypal.notification.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements ErrorCode {
    PERMISSION_UPDATE_NOTIFICATION_DENIED(HttpStatus.FORBIDDEN, "N0TI_001", "Some notifications could not be updated because they do not belong to your account."),
    PERMISSION_DELETE_NOTIFICATION_DENIED(HttpStatus.FORBIDDEN, "N0TI_002", "Some notifications could not be deleted because they do not belong to your account.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    NotificationErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
