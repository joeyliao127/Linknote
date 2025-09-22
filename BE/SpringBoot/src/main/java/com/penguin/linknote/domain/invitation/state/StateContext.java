package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.domain.invitation.state.enums.InvitationAction;
import com.penguin.linknote.entity.Invitation;

public interface StateContext {
    Invitation acceptInvitation(Invitation  invitation);
    Invitation rejectInvitation(Invitation invitation);
    Invitation resendInvitation(Invitation invitation);
}