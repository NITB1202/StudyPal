package com.study.studypal.common.exception.file;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileErrorCode implements ErrorCode {
    INVALID_IMAGE_FILE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_001", "Uploaded file is not a valid image."),
    INVALID_FILE_CONTENT(HttpStatus.BAD_REQUEST, "FILE_002", "Failed to read uploaded file.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    FileErrorCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
