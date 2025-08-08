package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.NotFoundException;

public class EmailNotFoundException extends NotFoundException {
    public EmailNotFoundException() {
        super("EMAIL_NOT_FOUND", "Email is not registered.");
    }
}
