package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.invitation.InvitationCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationStatus;
import com.penguin.linknote.entity.Invitation;
import com.penguin.linknote.entity.InvitationStatusCode;
import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.InvitationRepository;
import com.penguin.linknote.repository.InvitationStatusRepository;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.service.InvitationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
    public InvitationDTO createInvitation(UUID inviteeId, InvitationCommand invitationCommand) {
        Optional<User> invitee = userRepository.findByEmail(invitationCommand.getInviteeEmail());
        if(invitee.isEmpty()) throw new IllegalArgumentException("Email not found");

        Optional<Invitation> existInvitation = invitationRepository.findByInviterIdAndInviteeId(inviteeId, invitee.get().getId());
        if(existInvitation.isPresent()) return InvitationDTO.fromEntity(existInvitation.get());

        InvitationStatusCode status = invitationStatusRepository.findByTitle(InvitationStatus.PENDING.getTitle());

        Invitation invitation = new Invitation();

        invitation.setId(UUID.randomUUID());
        invitation.setInviterId(inviteeId);
        invitation.setInvitee(invitee.get());
        invitation.setNotebookId(invitationCommand.getNotebookId());
        invitation.setInvitationStatusCode(status);
        invitation.setMessage(invitationCommand.getMessage());
        invitation.setCreatedAt(Instant.now());
        invitation.setUpdatedAt(Instant.now());

        return InvitationDTO.fromEntity(invitationRepository.save(invitation));
    }

    @Override
    public InvitationDTO updateInvitation(UUID userId, InvitationCommand invitationCommand) {
        //TODO: state machine
        return null;
    }

    @Override
    public void deleteInvitation(UUID invitationId) {
        invitationRepository.deleteById(invitationId);
    }
}
