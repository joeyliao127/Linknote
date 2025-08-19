package com.penguin.linknote.controller.notebook;

import com.penguin.linknote.common.ApiResponse;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
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
    private String path;

    @Autowired
    public NotebookController(NotebookService notebookService) {
        this.notebookService = notebookService;
        this.path = "/notebooks";
    }

    @GetMapping
    public ResponseEntity<List<NotebookDTO>> index(@RequestParam(required = false) String title, @RequestParam(required = false) Boolean active) {
        List<NotebookDTO> notebookDTOList = notebookService.indexNotebooks(null, title, active);
        return ResponseEntity.ok(notebookDTOList);
    }

    @PostMapping
    public ResponseEntity<NotebookDTO> post(@RequestBody @Valid NotebookCommand notebookCommand) {
        UUID userId = UUID.fromString("a60b42ae-0659-4f89-aa31-0442b56642eb");
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
