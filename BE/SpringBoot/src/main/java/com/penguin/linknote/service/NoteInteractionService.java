package com.penguin.linknote.service;

import java.util.UUID;

public interface NoteInteractionService {
    void recordView(UUID userId, UUID noteId);
}
