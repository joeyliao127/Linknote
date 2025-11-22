package com.penguin.linknote.controller.notebook;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.NoteFilter;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.service.NoteService;
import com.penguin.linknote.service.NotebookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notebooks")
@Validated
public class NotebookController {

    private final NotebookService notebookService;
    private final NoteService noteService;
    private final String path;

    public NotebookController(NotebookService notebookService, NoteService noteService) {
        this.notebookService = notebookService;
        this.noteService = noteService;
        this.path = "/notebooks";
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotebookDTO>> index(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean active,
            Authentication authorization,
            PageCommand pageCommand)
    {
        // @ModelAttribute(忽略沒寫，用於pageCommand) 用於 Get Method，可以自動接收 Query String 到 Object 中，但 Object 必須有 no arg constructor
        UUID userId = (UUID) authorization.getPrincipal();
        PageResponse<NotebookDTO> notebookDTOList = notebookService.index(userId, title, active, pageCommand);
        return ResponseEntity.ok(notebookDTOList);
    }

    @GetMapping("/{notebookId}/notes")
    public ResponseEntity<PageResponse<NoteDTO>> get(@ModelAttribute NoteFilter  noteFilter, @PathVariable UUID notebookId, PageCommand pageCommand) {
        noteFilter.setNotebookId(notebookId);
        PageResponse<NoteDTO> noteList = noteService.indexNotes(noteFilter, pageCommand);
        return ResponseEntity.ok(noteList);
    }

    @PostMapping
    public ResponseEntity<NotebookDTO> create(@RequestBody @Valid NotebookCommand notebookCommand, @RequestHeader(name = "Authorization") UUID userId) {
        NotebookDTO notebook = notebookService.create(notebookCommand, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + this.path + "/" + notebook.getId()))
                .body(notebook);
    }

    @PutMapping("/{notebookId}")
    public ResponseEntity<NotebookDTO> update(@PathVariable UUID notebookId, @RequestBody @Valid NotebookCommand notebookCommand) {
        NotebookDTO notebookDTO = notebookService.update(notebookId, notebookCommand);

        return ResponseEntity.ok(notebookDTO);
    }

    @DeleteMapping("/{notebookId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID notebookId) {
        notebookService.delete(notebookId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete notebook successfully!"));
    }
}
