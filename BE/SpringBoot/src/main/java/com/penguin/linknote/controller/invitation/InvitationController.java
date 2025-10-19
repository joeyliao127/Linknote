package com.penguin.linknote.controller.invitation;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import com.penguin.linknote.service.InvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvitationDTO>> indexInvitations(PageCommand pageCommand, @RequestHeader(name = "Authorization") UUID userId) {
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInviter(userId, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @GetMapping("/sent")
    public ResponseEntity<PageResponse<InvitationDTO>> indexSentInvitations(PageCommand pageCommand, @RequestHeader(name = "Authorization") UUID userId) {
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInvitee(userId, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @PostMapping
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody @Valid InvitationCreateCommand createCommand, @RequestHeader(name = "Authorization") UUID userId) {
        InvitationDTO invitationDTO = invitationService.createInvitation(userId, createCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @PutMapping
    public ResponseEntity<InvitationDTO> updateInvitation(@RequestBody @Valid InvitationUpdateCommand updateCommand, @RequestHeader(name = "Authorization") UUID userId) {
        InvitationDTO invitationDTO = invitationService.updateInvitation(userId, updateCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteInvitation(UUID invitationId) {
        invitationService.deleteInvitation(invitationId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete invitation successfully!"));
    }


}
