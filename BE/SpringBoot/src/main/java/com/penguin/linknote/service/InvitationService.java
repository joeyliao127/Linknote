package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;

import java.util.UUID;

public interface InvitationService {

    PageResponse<InvitationDTO> indexByInviter(UUID userId, PageCommand pageCommand);
    PageResponse<InvitationDTO> indexByInvitee(UUID userId, PageCommand pageCommand);
    InvitationDTO createInvitation(UUID inviteeId, InvitationCreateCommand createCommand);
    InvitationDTO updateInvitation(UUID inviteeId, InvitationUpdateCommand updateCommand);
    void deleteInvitation(UUID invitationId);

}
