package com.penguin.linknote.domain.invitation;

import com.penguin.linknote.entity.Invitation;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class InvitationDTO {
    private String inviteeEmail;
    private String inviterEmail;
    private String message;
    private String status;

    public static InvitationDTO fromEntity(Invitation invitation) {
        return InvitationDTO.builder()
                .inviteeEmail(invitation.getInviteeEmail())
                .inviterEmail(invitation.getInviterEmail())
                .message(invitation.getMessage())
                .build();
    }

    public static List<InvitationDTO> fromEntity(List<Invitation> invitationList) {
        return invitationList.stream().map(InvitationDTO::fromEntity).toList();
    }
}
