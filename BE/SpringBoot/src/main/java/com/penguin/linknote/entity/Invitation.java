package com.penguin.linknote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="invitations")
@Data
public class Invitation {

    @Id
    private UUID id;

    //TODO: invitation email -> UserId

    @Column(name = "inviter_email")
    private String inviterEmail;

    @Column(name = "invitee_email")
    private String inviteeEmail;

    @Column(name = "message")
    private String message;

    @Column(name = "invitation_status_id")
    private Integer invitationStatusCode;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "notebook_id")
    private UUID notebookId;
}
