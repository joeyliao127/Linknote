package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.domain.ragnote.RagNoteCommand;
import com.penguin.linknote.domain.ragnote.RagNoteDTO;
import com.penguin.linknote.entity.RagNote;
import com.penguin.linknote.repository.RagNoteRepository;
import com.penguin.linknote.service.RagNoteService;

@Service
public class RagNoteServiceImpl implements RagNoteService {

    private final RagNoteRepository ragNoteRepository;

    public RagNoteServiceImpl(RagNoteRepository ragNoteRepository) {
        this.ragNoteRepository = ragNoteRepository;
    }

    @Override
    public List<RagNoteDTO> findByNotebookId(UUID notebookId, UUID userId) {
        return RagNoteDTO.fromEntityList(ragNoteRepository.findByNotebookId(notebookId, userId));
    }

    @Override
    public List<RagNoteDTO> findByUserId(UUID userId) {
        return RagNoteDTO.fromEntityList(ragNoteRepository.findByUserId(userId));
    }

    @Override
    public void delete(UUID noteId, UUID userId) {
        ragNoteRepository.delete(noteId, userId);
    }

    @Override
    public RagNoteDTO upsert(RagNoteCommand command, UUID userId) {
        RagNote ragNote = new RagNote();
        ragNote.setNoteId(command.getNoteId());
        ragNote.setUserId(userId);
        ragNote.setNotebookId(command.getNotebookId());
        ragNote.setNoteUpdatedAt(command.getNoteUpdatedAt());
        ragNote.setCreatedAt(Instant.now());
        return RagNoteDTO.fromEntity(ragNoteRepository.upsert(ragNote));
    }
}
