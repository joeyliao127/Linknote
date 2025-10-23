package com.penguin.linknote.service;

import java.util.List;
import java.util.UUID;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.NoteFilter;

public interface NoteService {
    // TODO: 支援 star, tag, order by desc 等 filters
    PageResponse<NoteDTO> indexNotes(NoteFilter filter, PageCommand pageCommand);

    NoteDTO get(UUID noteId);

    NoteDTO create(NoteCommand noteCommand);

    NoteDTO update(UUID noteId, NoteCommand noteCommand);

    void delete(UUID noteId);

    void addTagToNote(UUID noteId, List<UUID> tagIdList);
}