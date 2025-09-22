package com.penguin.linknote.controller.invitation;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.invitation.InvitationCreateCommand;
import com.penguin.linknote.domain.invitation.InvitationDTO;
import com.penguin.linknote.domain.invitation.InvitationUpdateCommand;
import com.penguin.linknote.service.InvitationService;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody @Valid InvitationCreateCommand createCommand) {
        InvitationDTO invitationDTO = invitationService.createInvitation(userId, createCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @PutMapping
    public ResponseEntity<InvitationDTO> updateInvitation(@RequestBody @Valid InvitationUpdateCommand updateCommand) {
        InvitationDTO invitationDTO = invitationService.updateInvitation(this.userId, updateCommand);
        return ResponseEntity.ok(invitationDTO);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteInvitation(UUID invitationId) {
        invitationService.deleteInvitation(invitationId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete invitation successfully!"));
    }


}
