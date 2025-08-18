package com.penguin.linknote.controller;

import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;
import com.penguin.linknote.entity.Notebook;
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

    @GetMapping("/test")
    public String test() {
        return "test update1";
    }

    @PostMapping("/test")
    public String test_post() {
        return "test post";
    }

    @PostMapping
    public ResponseEntity<Notebook> post(@RequestBody @Valid NotebookCommand notebookCommand) {

        // TODO: Get user id from token
        notebookCommand.setUserId(UUID.fromString("a60b42ae-0659-4f89-aa31-0442b56642eb"));
        Notebook notebook = notebookService.createNotebook(notebookCommand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + this.path + "/" + notebook.getId()))
                .body(notebook);
    }
}
