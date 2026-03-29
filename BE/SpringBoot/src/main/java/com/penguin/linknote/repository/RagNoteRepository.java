package com.penguin.linknote.repository;

import java.util.List;
import java.util.UUID;

import com.penguin.linknote.entity.RagNote;

public interface RagNoteRepository {
    List<RagNote> findByNotebookId(UUID notebookId, UUID userId);
    RagNote upsert(RagNote ragNote);
}
