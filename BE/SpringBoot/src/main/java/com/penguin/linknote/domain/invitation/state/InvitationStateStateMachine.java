package com.penguin.linknote.domain.invitation.state;

import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import com.penguin.linknote.entity.Invitation;

public class InvitationStateStateMachine implements StateContext {

    private final InvitationState pendingState;
    private final InvitationState acceptedState;
    private final InvitationState rejectedState;
    private final InvitationState resendState;
    private final InvitationState currentState;

    public InvitationStateStateMachine(InvitationStateEnum invitationStateEnum) {

        this.pendingState = new PendingState(this);
        this.acceptedState = new AcceptedState(this);
        this.rejectedState = new RejectedState(this);
        this.resendState = new ResnedState(this);

        switch (invitationStateEnum) {

            case InvitationStateEnum.ACCEPT -> {
                this.currentState = acceptedState;
            }
            case InvitationStateEnum.REJECT -> {
                this.currentState = rejectedState;
            }
            case InvitationStateEnum.RESEND -> {
                this.currentState = resendState;
            }
            default -> {
                this.currentState = pendingState;
            }
        }
    }

    @Override
    public Invitation acceptInvitation(Invitation invitation) {
        return currentState.acceptInvitation(invitation);
    }

    @Override
    public Invitation rejectInvitation(Invitation invitation) {
        return currentState.rejectInvitation(invitation);
    }

    @Override
    public Invitation resendInvitation(Invitation invitation) {
        return currentState.reSendInvitation(invitation);
    }
}
