package com.study.studypal.common.exception.domain.file;

import com.study.studypal.common.exception.base.BadRequestException;

public class InvalidImageException extends BadRequestException {
    public InvalidImageException() {
        super("INVALID_IMAGE", "Uploaded file is not a valid image.");
    }
}
