package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.entity.Invitation;

public interface InvitationState {
    Invitation acceptInvitation(Invitation invitation);
    Invitation rejectInvitation(Invitation invitation);
    Invitation reSendInvitation(Invitation invitation);
}
