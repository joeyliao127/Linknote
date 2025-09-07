package com.penguin.linknote.controller.invitation;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.service.InvitationService;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invitations")
public class InvitationController {

    private final InvitationService invitationService;

    private final UUID userId;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
        userId = UUID.fromString("abf76d59-c7d5-42b4-ab9d-b542993f7496");
    }

    @GetMapping
    public ResponseEntity<PageResponse<InvitationDTO>> indexInvitations(PageCommand pageCommand) {
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInviter(userId, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @GetMapping("/sent")
    public ResponseEntity<PageResponse<InvitationDTO>> indexSentInvitations(PageCommand pageCommand) {
        PageResponse<InvitationDTO> invitationDTOList = invitationService.indexByInvitee(userId, pageCommand);
        return ResponseEntity.ok(invitationDTOList);
    }

    @PostMapping
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody @Valid  InvitationCommand invitationCommand) {
        InvitationDTO invitationDTO = invitationService.createInvitation(userId, invitationCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @PutMapping
    public ResponseEntity<InvitationDTO> updateInvitation(UUID userId, InvitationCommand invitationCommand, ServletRequest servletRequest) {
        InvitationDTO invitationDTO = invitationService.updateInvitation(userId, invitationCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteInvitation(UUID invitationId) {
        invitationService.deleteInvitation(invitationId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete invitation successfully!"));
    }


}
