package com.penguin.linknote.domain.invitation;

import com.penguin.linknote.domain.invitation.state.enums.InvitationStateEnum;
import com.penguin.linknote.entity.Invitation;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class InvitationDTO {
    private UUID id;
    private String inviteeName;
    private String inviteeEmail;
    private String message;
    private InvitationStateEnum status;
    private UUID notebookId;
    private String notebookTitle;
    private String inviterName;

    public static InvitationDTO fromEntity(Invitation invitation) {
        return InvitationDTO.builder()
                .id(invitation.getId())
                .inviteeEmail(invitation.getInviteeEmail())
                .inviteeName(invitation.getInviteeName())
                .status(invitation.getStatus())
                .message(invitation.getMessage())
                .notebookId(invitation.getNotebookId())
                .notebookTitle(invitation.getNotebookTitle())
                .inviterName(invitation.getInviterName())
                .build();
    }

    public static List<InvitationDTO> fromEntity(List<Invitation> invitationList) {
        return invitationList.stream().map(InvitationDTO::fromEntity).toList();
    }
}
