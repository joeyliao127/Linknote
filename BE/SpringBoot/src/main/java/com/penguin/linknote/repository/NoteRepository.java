package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteCondition;
import com.penguin.linknote.entity.Note;

public interface NoteRepository {
    List<Note> index(UUID userId, UUID notebookId, Integer limit);

    PageResponse<Note> paginate(int page, int limit, NoteCondition condition);

    Optional<Note> get(UUID id);

    Note create(Note note);

    Note update(Note note);

    void delete(UUID id);
}
