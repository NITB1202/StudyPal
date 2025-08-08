package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException() {
        super("EMAIL_ALREADY_EXISTS", "Email is already registered.");
    }
}
