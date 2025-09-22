package com.penguin.linknote.domain.invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class InvitationCreateCommand {

    @NotBlank
    private String inviteeEmail;

    @NotNull
    private UUID notebookId;

    private String message;
}
