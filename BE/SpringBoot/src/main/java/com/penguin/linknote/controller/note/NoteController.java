package com.penguin.linknote.controller.note;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.NoteTagCommand;
import com.penguin.linknote.service.NoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDTO> get(@PathVariable UUID noteId) {
        NoteDTO noteDTO = noteService.get(noteId);
        return ResponseEntity.ok(noteDTO);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> create(@RequestBody @Valid NoteCommand noteCommand) {
        NoteDTO noteDTO = noteService.create(noteCommand);
        return ResponseEntity.ok(noteDTO);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDTO> update(@PathVariable UUID noteId, @RequestBody @Valid NoteCommand noteCommand) {
        NoteDTO noteDTO = noteService.update(noteId, noteCommand);
        return ResponseEntity.ok(noteDTO);
    }

    @DeleteMapping("{noteId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID noteId) {
        noteService.delete(noteId);
        return ResponseEntity.ok(new ApiResponse(true, "delete note successfully"));
    }

    @PutMapping("/{noteId}/tags")
    public ResponseEntity<ApiResponse> addTag(@PathVariable UUID noteId, @RequestBody @Valid NoteTagCommand command) {
        noteService.addTagToNote(noteId, command.getTagIdList().stream().map(UUID::fromString).toList());
        return ResponseEntity.ok(new ApiResponse(true, "Add tag successfully"));
    }

}
