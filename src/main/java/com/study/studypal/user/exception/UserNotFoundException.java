package com.study.studypal.user.exception;

import com.study.studypal.common.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("USER_NOT_FOUND", "User not found.");
    }
}
