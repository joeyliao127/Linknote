package com.penguin.linknote.service;

import java.util.List;
import java.util.UUID;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteCondition;
import com.penguin.linknote.domain.note.NoteDTO;

public interface NoteService {
    PageResponse<NoteDTO> indexNotes(NoteCondition condition, PageCommand pageCommand);

    NoteDTO get(UUID noteId);

    NoteDTO create(NoteCommand noteCommand);

    NoteDTO update(UUID noteId, NoteCommand noteCommand);

    void delete(UUID noteId);

    void addTagToNote(UUID noteId, List<UUID> tagIdList);
}
