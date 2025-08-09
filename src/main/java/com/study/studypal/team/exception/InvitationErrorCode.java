package com.study.studypal.team.exception;

import com.study.studypal.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InvitationErrorCode implements ErrorCode {
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INV_001", "Invitation not found."),
    INVITEE_ALREADY_INVITED(HttpStatus.CONFLICT, "INV_002", "The invitee has already been invited to this team."),
    PERMISSION_REPLY_INVITATION_DENIED(HttpStatus.FORBIDDEN, "INV_003", "You cannot respond to someone else's invitation.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    InvitationErrorCode(final HttpStatus httpStatus, final String code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
