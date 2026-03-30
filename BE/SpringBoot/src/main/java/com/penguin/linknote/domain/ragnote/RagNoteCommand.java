package com.penguin.linknote.domain.ragnote;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RagNoteCommand {
    @NotNull
    private UUID noteId;

    @NotNull
    private UUID notebookId;

    @NotNull
    private Instant noteUpdatedAt;
}
