package com.penguin.linknote.entity;

import com.penguin.linknote.domain.interaction.InteractionAction;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class NoteInteraction {
    private Long id;
    private UUID userId;
    private UUID noteId;
    private InteractionAction action;
    private Instant actedAt;

    public NoteInteraction() {
        this.actedAt = Instant.now();
    }
}
