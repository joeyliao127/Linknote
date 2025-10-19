package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;

import java.util.UUID;

public interface NotebookService {
    PageResponse<NotebookDTO> indexNotebooks(UUID userId, String title, Boolean active, PageCommand pageCommand);

    NotebookDTO getNotebookById(UUID notebookId);

    NotebookDTO createNotebook(NotebookCommand notebookCommand, UUID userId);

    NotebookDTO updateNotebook(UUID notebookId, NotebookCommand notebookCommand);

    void deleteNotebook(UUID notebookId);
}




