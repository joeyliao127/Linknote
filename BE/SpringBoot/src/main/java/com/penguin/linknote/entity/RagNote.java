package com.penguin.linknote.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class RagNote {
    private UUID noteId;
    private UUID userId;
    private UUID notebookId;
    private Instant noteUpdatedAt;
    private Instant createdAt;
}
