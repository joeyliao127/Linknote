package com.penguin.linknote.repository;

import com.penguin.linknote.entity.NoteTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteTagsRepository extends JpaRepository<NoteTag, UUID> {
}
