package com.study.studypal.auth.exception;

import com.study.studypal.common.exception.base.BadRequestException;

public class ThirdPartyLoginException extends BadRequestException {
    public ThirdPartyLoginException() {
        super("THIRD_PARTY_LOGIN", "This account was created through a third-party login. Please sign in using your linked provider.");
    }
}
