package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import com.penguin.linknote.domain.invitation.state.InvitationStateStateMachine;
import com.penguin.linknote.domain.invitation.state.enums.InvitationAction;
import com.penguin.linknote.entity.Invitation;
import com.penguin.linknote.entity.InvitationStatusCode;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.InvitationRepository;
import com.penguin.linknote.repository.InvitationStatusRepository;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.InvitationService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final InvitationStatusRepository invitationStatusRepository;
    private final UserRepository userRepository;
    private final PaginationService paginationService;

    public InvitationServiceImpl(
            InvitationRepository invitationRepository,
            InvitationStatusRepository invitationStatusRepository,
            UserRepository userRepository,
            PaginationService paginationService)
    {
        this.invitationRepository = invitationRepository;
        this.invitationStatusRepository = invitationStatusRepository;
        this.userRepository = userRepository;
        this.paginationService = paginationService;
    }

    @Override
    public PageResponse<InvitationDTO> indexByInviter(UUID userId, PageCommand pageCommand) {
        PageCommand normalizePageCommand = paginationService.normalizePageCommand(pageCommand);
        Pageable pageable = PageRequest.of(normalizePageCommand.getPage(), normalizePageCommand.getPageSize());
        Page<Invitation> invitationPage = invitationRepository.findByInviterId(userId, pageable);

        return PageResponse.fromPage(invitationPage, InvitationDTO::fromEntity);
    }

    @Override
    public PageResponse<InvitationDTO> indexByInvitee(UUID userId, PageCommand pageCommand) {
        PageCommand normalizePageCommand = paginationService.normalizePageCommand(pageCommand);
        Pageable pageable = PageRequest.of(normalizePageCommand.getPage(), normalizePageCommand.getPageSize());
        Page<Invitation> invitationPage = invitationRepository.findByInviteeId(userId, pageable);

        return PageResponse.fromPage(invitationPage, InvitationDTO::fromEntity);
    }

    @Override
    public InvitationDTO create(UUID inviteeId, InvitationCreateCommand createCommand) {
        Optional<User> invitee = userRepository.findByEmail(createCommand.getInviteeEmail());
        if(invitee.isEmpty()) throw new IllegalArgumentException("Email not found");

        Optional<Invitation> existInvitation = invitationRepository.findByInviterIdAndInviteeId(inviteeId, invitee.get().getId());
        if(existInvitation.isPresent()) return InvitationDTO.fromEntity(existInvitation.get());

        InvitationStatusCode status = invitationStatusRepository.findByTitle(InvitationAction.PENDING.getTitle());

        Invitation invitation = new Invitation();

        invitation.setId(UUID.randomUUID());
        invitation.setInviterId(inviteeId);
        invitation.setInvitee(invitee.get());
        invitation.setNotebookId(createCommand.getNotebookId());
        invitation.setInvitationStatusCode(status);
        invitation.setMessage(createCommand.getMessage());
        invitation.setCreatedAt(Instant.now());
        invitation.setUpdatedAt(Instant.now());

        return InvitationDTO.fromEntity(invitationRepository.save(invitation));
    }

    @Override
    public InvitationDTO update(UUID inviteeId, InvitationUpdateCommand updateCommand) {

        Invitation invitation = invitationRepository.findByIdAndInviteeId(updateCommand.getInvitationId(), inviteeId).orElseThrow(()-> new EntityNotFoundException("Invitation not found"));
        Invitation updatedInvitation = null;

        InvitationStateStateMachine invitationStateMachine = new InvitationStateStateMachine(invitation.getStatus());

        switch (updateCommand.getStatus()) {
            case ACCEPT -> {
                updatedInvitation = invitationRepository.save(invitationStateMachine.acceptInvitation(invitation));
            }
            case REJECT -> {
                updatedInvitation = invitationRepository.save(invitationStateMachine.rejectInvitation(invitation));
            }

            case RESEND -> {
                updatedInvitation = invitationRepository.save(invitationStateMachine.resendInvitation(invitation));
            }
        }

        return InvitationDTO.fromEntity(updatedInvitation);
    }

    @Override
    public void delete(UUID invitationId) {
        invitationRepository.deleteById(invitationId);
    }
}
