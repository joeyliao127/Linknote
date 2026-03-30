package com.penguin.linknote.service.impl;

import com.penguin.linknote.domain.interaction.InteractionAction;
import com.penguin.linknote.entity.NoteInteraction;
import com.penguin.linknote.repository.NoteInteractionRepository;
import com.penguin.linknote.repository.NoteRepository;
import com.penguin.linknote.service.NoteInteractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class NoteInteractionServiceImpl implements NoteInteractionService {

    private final NoteInteractionRepository noteInteractionRepository;
    private final NoteRepository noteRepository;

    public NoteInteractionServiceImpl(NoteInteractionRepository noteInteractionRepository,
                                      NoteRepository noteRepository) {
        this.noteInteractionRepository = noteInteractionRepository;
        this.noteRepository = noteRepository;
    }

    @Async("interactionExecutor")
    @Override
    public void recordView(UUID userId, UUID noteId) {
        try {
            NoteInteraction interaction = new NoteInteraction();
            interaction.setUserId(userId);
            interaction.setNoteId(noteId);
            interaction.setAction(InteractionAction.VIEW);

            int rowsInserted = noteInteractionRepository.record(interaction);
            if (rowsInserted == 1) {
                noteRepository.incrementViewCount(noteId);
            }
        } catch (Exception e) {
            log.warn("Failed to record view interaction for note {}: {}", noteId, e.getMessage());
        }
    }
}
