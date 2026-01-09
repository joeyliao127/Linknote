package com.penguin.linknote.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.penguin.linknote.entity.NoteTag;
import com.penguin.linknote.entity.Tag;

public interface NoteTagsRepository {
    void deleteByNoteId(UUID noteId);

    void saveAll(List<NoteTag> noteTags);

    List<Tag> findTagsByNoteId(UUID noteId);

    Map<UUID, List<Tag>> findTagsByNoteIds(List<UUID> noteIds);
}
