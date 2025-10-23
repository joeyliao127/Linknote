package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;

public interface InvitationService {

    PageResponse<InvitationDTO> indexByInviter(UUID userId, PageCommand pageCommand);
    PageResponse<InvitationDTO> indexByInvitee(UUID userId, PageCommand pageCommand);
    InvitationDTO create(UUID inviteeId, InvitationCreateCommand createCommand);
    InvitationDTO update(UUID inviteeId, InvitationUpdateCommand updateCommand);
    void delete(UUID invitationId);

}
