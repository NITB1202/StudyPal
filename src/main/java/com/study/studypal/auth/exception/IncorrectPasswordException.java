package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.BadRequestException;

public class IncorrectPasswordException extends BadRequestException {
    public IncorrectPasswordException() {
        super("INCORRECT_PASSWORD", "Incorrect password.");
    }
}
