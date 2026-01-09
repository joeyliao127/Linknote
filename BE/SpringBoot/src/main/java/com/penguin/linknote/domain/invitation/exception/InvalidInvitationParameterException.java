package com.penguin.linknote.domain.invitation.exception;

public class InvalidInvitationParameterException extends RuntimeException {
    public InvalidInvitationParameterException() {
        super("無效的參數");
    }

    public InvalidInvitationParameterException(String message) {
        super(message);
    }
}
