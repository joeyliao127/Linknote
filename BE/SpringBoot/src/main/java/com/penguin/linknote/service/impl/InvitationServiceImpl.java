package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCondition;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import com.penguin.linknote.domain.invitation.exception.InvitationNotFoundException;
import com.penguin.linknote.domain.invitation.state.InvitationStateStateMachine;
import com.penguin.linknote.domain.invitation.state.enums.InvitationAction;
import com.penguin.linknote.entity.Invitation;
import com.penguin.linknote.entity.InvitationStatusCode;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.InvitationRepository;
import com.penguin.linknote.repository.InvitationStatusRepository;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.InvitationService;

@Service
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final InvitationStatusRepository invitationStatusRepository;
    private final UserRepository userRepository;

    public InvitationServiceImpl(
            InvitationRepository invitationRepository,
            InvitationStatusRepository invitationStatusRepository,
            UserRepository userRepository)
    {
        this.invitationRepository = invitationRepository;
        this.invitationStatusRepository = invitationStatusRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PageResponse<InvitationDTO> indexByInviter(InvitationCondition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        PageResponse<Invitation> invitationPage = invitationRepository.paginateByInviter(page, limit, condition);

        PageResponse<InvitationDTO> response = new PageResponse<>();
        response.setCount(invitationPage.getCount());
        response.setCurrentPage(invitationPage.getCurrentPage());
        response.setPageSize(invitationPage.getPageSize());
        response.setTotalPage(invitationPage.getTotalPage());
        response.setItems(invitationPage.getItems().stream().map(InvitationDTO::fromEntity).toList());
        return response;
    }

    @Override
    public PageResponse<InvitationDTO> indexByInvitee(InvitationCondition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        PageResponse<Invitation> invitationPage = invitationRepository.paginateByInvitee(page, limit, condition);

        PageResponse<InvitationDTO> response = new PageResponse<>();
        response.setCount(invitationPage.getCount());
        response.setCurrentPage(invitationPage.getCurrentPage());
        response.setPageSize(invitationPage.getPageSize());
        response.setTotalPage(invitationPage.getTotalPage());
        response.setItems(invitationPage.getItems().stream().map(InvitationDTO::fromEntity).toList());
        return response;
    }

    @Override
    public InvitationDTO create(UUID inviteeId, InvitationCreateCommand createCommand) {
        Optional<User> invitee = userRepository.findByEmail(createCommand.getInviteeEmail());
        if(invitee.isEmpty()) throw new IllegalArgumentException("Email not found");

        // 確認 inivter 是否已經邀請過 invitee，避免重複邀請
        // TODO: 但事後要補上若 status = reject，將狀態轉為 pending
        Optional<Invitation> existInvitation = invitationRepository.getByInviterAndInvitee(inviteeId, invitee.get().getId());
        if(existInvitation.isPresent()) return InvitationDTO.fromEntity(existInvitation.get());

        InvitationStatusCode status = invitationStatusRepository.findByTitle(InvitationAction.PENDING.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("Invitation status not found"));

        Invitation invitation = new Invitation();

        invitation.setId(UUID.randomUUID());
        invitation.setInviterId(inviteeId);
        invitation.setInvitee(invitee.get());
        invitation.setNotebookId(createCommand.getNotebookId());
        invitation.setInvitationStatusCode(status);
        invitation.setMessage(createCommand.getMessage());
        invitation.setCreatedAt(Instant.now());
        invitation.setUpdatedAt(Instant.now());

        return InvitationDTO.fromEntity(invitationRepository.create(invitation));
    }

    @Override
    public InvitationDTO update(UUID inviteeId, InvitationUpdateCommand updateCommand) {

        Invitation invitation = invitationRepository.getByInvitee(updateCommand.getInvitationId(), inviteeId)
                .orElseThrow(() -> new InvitationNotFoundException("Invitation not found"));
        Invitation updatedInvitation = null;

        InvitationStateStateMachine invitationStateMachine = new InvitationStateStateMachine(invitation.getStatus());

        switch (updateCommand.getStatus()) {
            case ACCEPT -> {
                updatedInvitation = invitationStateMachine.acceptInvitation(invitation);
            }
            case REJECT -> {
                updatedInvitation = invitationStateMachine.rejectInvitation(invitation);
            }

            case RESEND -> {
                updatedInvitation = invitationStateMachine.resendInvitation(invitation);
            }
        }

        InvitationStatusCode status = invitationStatusRepository
                .findByTitle(updatedInvitation.getInvitationStatusCode().getTitle())
                .orElseThrow(() -> new IllegalArgumentException("Invitation status not found"));
        updatedInvitation.setInvitationStatusCode(status);
        updatedInvitation.setUpdatedAt(Instant.now());

        return InvitationDTO.fromEntity(invitationRepository.update(updatedInvitation));
    }

    @Override
    public void delete(UUID invitationId) {
        invitationRepository.delete(invitationId);
    }
}
