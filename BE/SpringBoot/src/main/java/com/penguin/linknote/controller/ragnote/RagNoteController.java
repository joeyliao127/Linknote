package com.penguin.linknote.controller.ragnote;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.domain.ragnote.RagNoteCommand;
import com.penguin.linknote.domain.ragnote.RagNoteDTO;
import com.penguin.linknote.service.RagNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rag-notes")
public class RagNoteController {

    private final RagNoteService ragNoteService;

    public RagNoteController(RagNoteService ragNoteService) {
        this.ragNoteService = ragNoteService;
    }

    @PostMapping
    public ResponseEntity<RagNoteDTO> upsert(
            @RequestBody @Valid RagNoteCommand command,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(ragNoteService.upsert(command, userId));
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID noteId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        ragNoteService.delete(noteId, userId);
    }

    @GetMapping
    public ResponseEntity<List<RagNoteDTO>> findByNotebookId(
            @RequestParam UUID notebookId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(ragNoteService.findByNotebookId(notebookId, userId));
    }
}
