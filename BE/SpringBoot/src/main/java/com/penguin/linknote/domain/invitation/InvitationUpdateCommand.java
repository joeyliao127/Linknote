package com.penguin.linknote.domain.invitation;

import com.penguin.linknote.domain.invitation.state.enums.InvitationAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class InvitationUpdateCommand {

    @NotNull
    private UUID invitationId;

    private InvitationAction status;
}