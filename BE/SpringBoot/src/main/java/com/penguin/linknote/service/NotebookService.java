package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;

public interface NotebookService {
    PageResponse<NotebookDTO> index(UUID userId, String title, Boolean active, PageCommand pageCommand);

    NotebookDTO getNotebookById(UUID notebookId);

    NotebookDTO create(NotebookCommand notebookCommand, UUID userId);

    NotebookDTO update(UUID notebookId, NotebookCommand notebookCommand);

    void delete(UUID notebookId);
}




