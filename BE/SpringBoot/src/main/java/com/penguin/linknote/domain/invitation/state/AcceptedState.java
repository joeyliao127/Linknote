package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.common.exception.invitation.InvalidInvitationActionException;
import com.penguin.linknote.entity.Invitation;

public class AcceptedState implements InvitationState {
    private final StateContext handler;

    public AcceptedState(StateContext handler) {
        this.handler = handler;
    }

    @Override
    public Invitation acceptInvitation(Invitation invitation) {
        throw new InvalidInvitationActionException("Invalid invitation action");
    }

    @Override
    public Invitation rejectInvitation(Invitation invitation) {
        throw new InvalidInvitationActionException("Invalid invitation action");
    }

    @Override
    public Invitation reSendInvitation(Invitation invitation) {
        throw new InvalidInvitationActionException("Invalid invitation action");
    }
}
