package com.penguin.linknote.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="invitations")
@Data
public class Invitation {

    @Id
    private UUID id;

    @Column(name = "inviter_id")
    private UUID inviterId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invitation_status_id")
    private InvitationStatusCode invitationStatusCode;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "notebook_id")
    private UUID notebookId;

    public String getStatusTitle() {
        return invitationStatusCode.getTitle();
    }

    public String getInviteeName() {
        return invitee.getUsername();
    }

    public String getInviteeEmail() {
        return invitee.getEmail();
    }
}
