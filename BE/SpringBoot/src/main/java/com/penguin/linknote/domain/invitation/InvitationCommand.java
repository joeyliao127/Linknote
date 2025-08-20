package com.penguin.linknote.domain.invitation;


import com.penguin.linknote.entity.Notebook;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvitationCommand {
    private String inviteeEmail;
    private String message;
}
