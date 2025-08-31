package com.penguin.linknote.domain.invitation;

import com.penguin.linknote.entity.Invitation;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class InvitationDTO {
    private UUID inviteeId;
    private UUID inviterId;
    private String message;
    private String status;

    public static InvitationDTO fromEntity(Invitation invitation) {
        return InvitationDTO.builder()
                .inviteeId(invitation.getInviteeId())
                .inviterId(invitation.getInviterId())
                .inviterId(invitation.getInviterId())
                .message(invitation.getMessage())
                .build();
    }

    public static List<InvitationDTO> fromEntity(List<Invitation> invitationList) {
        return invitationList.stream().map(InvitationDTO::fromEntity).toList();
    }
}
