package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.domain.invitation.exception.InvalidInvitationActionException;
import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import com.penguin.linknote.entity.Invitation;

public class RejectedState implements InvitationState {

    private final StateContext handler;

    public RejectedState(StateContext handler) {
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
        invitation.setStatus(InvitationStateEnum.RESEND);
        return invitation;
    }
}
