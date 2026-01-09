package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCondition;
import com.penguin.linknote.entity.Invitation;

public interface InvitationRepository {
    List<Invitation> indexByInviter(UUID userId, Integer limit);

    List<Invitation> indexByInvitee(UUID userId, Integer limit);

    PageResponse<Invitation> paginateByInviter(int page, int limit, InvitationCondition condition);

    PageResponse<Invitation> paginateByInvitee(int page, int limit, InvitationCondition condition);

    Optional<Invitation> get(UUID id);

    Optional<Invitation> getByInvitee(UUID id, UUID inviteeId);

    Optional<Invitation> getByInviterAndInvitee(UUID inviterId, UUID inviteeId);

    Invitation create(Invitation invitation);

    Invitation update(Invitation invitation);

    void delete(UUID id);
}
