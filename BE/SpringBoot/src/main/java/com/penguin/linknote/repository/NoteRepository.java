package com.penguin.linknote.repository;

import com.penguin.linknote.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {
    Page<Note> findByNotebookId(UUID notebookId, Pageable pageable);
}
