package com.penguin.linknote.repository;

import com.penguin.linknote.entity.NoteTag;
import com.penguin.linknote.entity.NoteTagId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NoteTagsRepository extends JpaRepository<NoteTag, NoteTagId> {
    List<NoteTag> findByNoteId(UUID noteId);
    void deleteByNoteId(UUID noteId);

    @Modifying
    @Query("DELETE FROM NoteTag nt WHERE nt.note.id = :noteId")
    void deleteByNoteIdJpql(@Param("noteId") UUID noteId);
}
