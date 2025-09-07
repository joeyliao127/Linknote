package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;

import java.util.UUID;

public interface InvitationService {

    PageResponse<InvitationDTO> indexByInviter(UUID userId, PageCommand pageCommand);
    PageResponse<InvitationDTO> indexByInvitee(UUID userId, PageCommand pageCommand);
    InvitationDTO createInvitation(UUID inviteeId, InvitationCommand invitationCommand);
    InvitationDTO updateInvitation(UUID userId, InvitationCommand invitationCommand);
    void deleteInvitation(UUID invitationId);

}
