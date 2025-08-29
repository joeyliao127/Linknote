package com.penguin.linknote.service;

import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    // TODO: 支援 star, tag, order by desc 等 filters
    List<NoteDTO> indexNotesByNotebookId(UUID notebookId);

    NoteDTO getNoteById(UUID noteId);

    NoteDTO createNote(NoteCommand noteCommand);

    NoteDTO updateNote(UUID noteId, NoteCommand noteCommand);

    void deleteNote(UUID noteId);
}