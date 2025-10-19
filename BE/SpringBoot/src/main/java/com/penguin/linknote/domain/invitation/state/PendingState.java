package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.domain.invitation.exception.InvalidInvitationStateException;
import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import com.penguin.linknote.entity.Invitation;

public class PendingState implements InvitationState {

    private final StateContext handler;

    public PendingState(StateContext handler) {
        this.handler = handler;
    }

    @Override
    public Invitation acceptInvitation(Invitation invitation) {
        invitation.setStatus(InvitationStateEnum.ACCEPT);
        return invitation;
    }

    @Override
    public Invitation rejectInvitation(Invitation invitation) {
        invitation.setStatus(InvitationStateEnum.REJECT);
        return invitation;
    }

    @Override
    public Invitation reSendInvitation(Invitation invitation) {
        throw new InvalidInvitationStateException("Invalid invitation state, Operation: pending to re-send");
    }
}
