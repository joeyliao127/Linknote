package com.penguin.linknote.domain.invitation.exception;

public class InvitationNotFoundException extends RuntimeException {
    public InvitationNotFoundException() {
        super("Invitation not found");
    }

    public InvitationNotFoundException(String message) {
        super(message);
    }
}
