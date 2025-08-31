package com.penguin.linknote.controller.notebook;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.service.NoteService;
import com.penguin.linknote.service.NotebookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notebooks")
public class NotebookController {

    private final NotebookService notebookService;
    private final NoteService noteService;
    private final String path;

    @Autowired
    public NotebookController(NotebookService notebookService, NoteService noteService) {
        this.notebookService = notebookService;
        this.noteService = noteService;
        this.path = "/notebooks";
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotebookDTO>> index(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean active,
            PageCommand pageCommand)
    {
        // @ModelAttribute(忽略沒寫，用於pageCommand) 用於 Get Method，可以自動接收 Query String 到 Object 中，但 Object 必須有 no arg constructor
        PageResponse<NotebookDTO> notebookDTOList = notebookService.indexNotebooks(null, title, active, pageCommand);
        return ResponseEntity.ok(notebookDTOList);
    }

    @GetMapping("/{notebookId}/notes")
    public ResponseEntity<List<NoteDTO>> list(@PathVariable UUID notebookId) {
        List<NoteDTO> noteList = noteService.indexNotesByNotebookId(notebookId);
        return ResponseEntity.ok(noteList);
    }

    @PostMapping
    public ResponseEntity<NotebookDTO> create(@RequestBody @Valid NotebookCommand notebookCommand) {
        UUID userId = UUID.fromString("abf76d59-c7d5-42b4-ab9d-b542993f7496");
        NotebookDTO notebook = notebookService.createNotebook(notebookCommand, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + this.path + "/" + notebook.getId()))
                .body(notebook);
    }

    @PutMapping("/{notebookId}")
    public ResponseEntity<NotebookDTO> update(@PathVariable UUID notebookId, @RequestBody @Valid NotebookCommand notebookCommand) {
        NotebookDTO notebookDTO = notebookService.updateNotebook(notebookId, notebookCommand);

        return ResponseEntity.ok(notebookDTO);
    }

    @DeleteMapping("/{notebookId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID notebookId) {
        notebookService.deleteNotebook(notebookId);
        return ResponseEntity.ok(new ApiResponse(true, "Delete notebook successfully!"));
    }
}
