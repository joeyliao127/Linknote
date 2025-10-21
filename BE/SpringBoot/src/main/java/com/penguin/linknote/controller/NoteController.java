package com.penguin.linknote.controller.note;

import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.NoteTagCommand;
import com.penguin.linknote.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDTO> getNote(@PathVariable UUID noteId) {
        NoteDTO noteDTO = noteService.getNoteById(noteId);
        return ResponseEntity.ok(noteDTO);
    }

    @PostMapping
    public ResponseEntity<NoteDTO> create(@RequestBody @Valid NoteCommand noteCommand) {
        NoteDTO noteDTO = noteService.createNote(noteCommand);
        return ResponseEntity.ok(noteDTO);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDTO> update(@PathVariable UUID noteId, @RequestBody @Valid NoteCommand noteCommand) {
        NoteDTO noteDTO = noteService.updateNote(noteId, noteCommand);
        return ResponseEntity.ok(noteDTO);
    }

    @DeleteMapping("{noteId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.ok(new ApiResponse(true, "delete note successfully"));
    }

    @PutMapping("/{noteId}/tags")
    public ResponseEntity<ApiResponse> addTag(@PathVariable UUID noteId, @RequestBody @Valid NoteTagCommand command) {
        noteService.addTagToNote(noteId, command.getTagIdList().stream().map(UUID::fromString).toList());
        return ResponseEntity.ok(new ApiResponse(true, "Add tag successfully"));
    }

}
