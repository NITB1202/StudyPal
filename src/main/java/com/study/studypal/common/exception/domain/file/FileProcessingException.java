package com.study.studypal.common.exception.domain.file;

import com.study.studypal.common.exception.base.InternalServerException;

public class FileProcessingException extends InternalServerException {
    public FileProcessingException(Throwable cause) {
        super("FILE_PROCESSING_FAILED", "Failed to read uploaded file.");
        initCause(cause);
    }
}