package com.study.studypal.common.exception.errorCode;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConfigErrorCode implements ErrorCode {
    MISSING_CACHE_CONFIG(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_001", "Missing cache specs in configuration."),
    MISSING_JOB_PACKAGES(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_002", "Missing quartz.job-packages in configuration."),
    INVALID_JOB_CLASS(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_003", "Invalid job class: %s"),
    CLASS_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_004", "Class not found: %s"),
    REGISTER_JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_005", "Failed to register job: %s");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ConfigErrorCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
