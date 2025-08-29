package com.penguin.linknote.repository;

import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<NoteDTO> findByNotebookId(UUID notebookId);
}
