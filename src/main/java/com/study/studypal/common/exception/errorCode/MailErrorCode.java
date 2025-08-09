package com.study.studypal.common.exception.errorCode;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MailErrorCode implements ErrorCode {
    EMAIL_EMPTY(HttpStatus.BAD_REQUEST, "MAIL_001", "The recipient's email address is empty."),
    SEND_EMAIL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_002", "Failed to send email.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MailErrorCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
