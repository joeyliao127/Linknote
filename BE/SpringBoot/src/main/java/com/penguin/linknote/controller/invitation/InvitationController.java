package com.penguin.linknote.controller.invitation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCondition;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationOrderBy;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import com.penguin.linknote.domain.invitation.exception.InvalidInvitationParameterException;
import com.penguin.linknote.service.InvitationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvitationDTO>> indexInvitations(
            @ModelAttribute InvitationCondition condition,
            PageCommand pageCommand,
            Authentication authentication) {
        if (condition == null) {
            condition = new InvitationCondition();
        }
        if (condition.getOrderBy() != null && !condition.getOrderBy().isBlank()) {
            if (InvitationOrderBy.from(condition.getOrderBy()) == null) {
                throw new InvalidInvitationParameterException();
            }
        }
        UUID userId = (UUID) authentication.getPrincipal();
        condition.setUserId(userId);
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInvitee(condition, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @GetMapping("/sent")
    public ResponseEntity<PageResponse<InvitationDTO>> indexSentInvitations(
            @ModelAttribute InvitationCondition condition,
            PageCommand pageCommand,
            Authentication authentication) {
        if (condition == null) {
            condition = new InvitationCondition();
        }
        if (condition.getOrderBy() != null && !condition.getOrderBy().isBlank()) {
            if (InvitationOrderBy.from(condition.getOrderBy()) == null) {
                throw new InvalidInvitationParameterException();
            }
        }
        UUID userId = (UUID) authentication.getPrincipal();
        condition.setUserId(userId);
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInviter(condition, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @PostMapping
    public ResponseEntity<InvitationDTO> create(@RequestBody @Valid InvitationCreateCommand createCommand, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        InvitationDTO invitationDTO = invitationService.create(userId, createCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @PutMapping
    public ResponseEntity<InvitationDTO> update(@RequestBody @Valid InvitationUpdateCommand updateCommand, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        InvitationDTO invitationDTO = invitationService.update(userId, updateCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> delete(UUID invitationId) {
        invitationService.delete(invitationId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete invitation successfully!"));
    }


}
