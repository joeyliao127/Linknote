package com.penguin.linknote.repository;

import com.penguin.linknote.entity.NoteInteraction;

public interface NoteInteractionRepository {
    /**
     * Records an interaction. Returns 1 if inserted (new), 0 if deduplicated (same user+note+action within the same minute).
     */
    int record(NoteInteraction interaction);
}
