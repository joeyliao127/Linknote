package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.common.exception.invitation.InvalidInvitationStateException;
import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import com.penguin.linknote.entity.Invitation;

public class ResnedState implements InvitationState {

    private StateContext stateContext;
    public ResnedState(StateContext stateContext) {
        this.stateContext = stateContext;
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
        throw new InvalidInvitationStateException("Invitation state cannot be re-sent");
    }
}
