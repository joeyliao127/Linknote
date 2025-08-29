package com.penguin.linknote.service;

import com.penguin.linknote.domain.notebook.NotebookCommand;
import com.penguin.linknote.domain.notebook.NotebookDTO;

import java.util.List;
import java.util.UUID;

public interface NotebookService {
    List<NotebookDTO> indexNotebooks(UUID userId, String title, Boolean active);

    NotebookDTO getNotebookById(UUID notebookId);

    NotebookDTO createNotebook(NotebookCommand notebookCommand, UUID userId);

    NotebookDTO updateNotebook(UUID notebookId, NotebookCommand notebookCommand);

    void deleteNotebook(UUID notebookId);
}




