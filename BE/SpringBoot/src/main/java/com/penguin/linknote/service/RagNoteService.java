package com.penguin.linknote.service;

import java.util.List;
import java.util.UUID;

import com.penguin.linknote.domain.ragnote.RagNoteCommand;
import com.penguin.linknote.domain.ragnote.RagNoteDTO;

public interface RagNoteService {
    List<RagNoteDTO> findByNotebookId(UUID notebookId, UUID userId);
    RagNoteDTO upsert(RagNoteCommand command, UUID userId);
}
