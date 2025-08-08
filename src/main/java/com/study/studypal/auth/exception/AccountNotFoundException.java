package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.NotFoundException;

import java.util.UUID;

public class AccountNotFoundException  extends NotFoundException {
    public AccountNotFoundException(UUID userId) {
        super("ACCOUNT_NOT_FOUND", "Account not found for userId: " + userId);
    }
}
