package com.study.studypal.common.exception.domain.user;

import com.study.studypal.common.exception.base.NotFoundException;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID userId) {
        super("USER_NOT_FOUND", "User with ID " + userId + " not found.");
    }
}
