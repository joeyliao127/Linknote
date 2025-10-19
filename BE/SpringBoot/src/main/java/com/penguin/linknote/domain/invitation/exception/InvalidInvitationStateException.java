package com.penguin.linknote.domain.invitation.exception;

public class InvalidInvitationStateException extends RuntimeException {
    public InvalidInvitationStateException(String message) {
        super(message);
    }
}
