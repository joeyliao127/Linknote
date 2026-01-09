package com.penguin.linknote.entity;

import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Invitation {

    private UUID id;

    private UUID inviterId;

    private User invitee;

    private String message;

    private InvitationStatusCode invitationStatusCode;

    private Instant createdAt;

    private Instant updatedAt;

    private UUID notebookId;

    public InvitationStateEnum getStatus() {
        return InvitationStateEnum.fromTitle(invitationStatusCode.getTitle());
    }

    public String getInviteeName() {
        return invitee.getUsername();
    }

    public String getInviteeEmail() {
        return invitee.getEmail();
    }

    public void setStatus(InvitationStateEnum invitationStatusCode) {
        this.invitationStatusCode.setTitle(invitationStatusCode.getTitle());
    }
}
