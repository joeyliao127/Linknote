package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCondition;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import java.util.UUID;

public interface InvitationService {

    PageResponse<InvitationDTO> indexByInviter(InvitationCondition condition, PageCommand pageCommand);
    PageResponse<InvitationDTO> indexByInvitee(InvitationCondition condition, PageCommand pageCommand);
    InvitationDTO create(UUID inviteeId, InvitationCreateCommand createCommand);
    InvitationDTO update(UUID inviteeId, InvitationUpdateCommand updateCommand);
    void delete(UUID invitationId);

}
