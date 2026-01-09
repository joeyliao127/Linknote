package com.penguin.linknote.domain.note;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class NoteCondition {
    private UUID userId;
    private UUID notebookId;
    private String title;
    private Boolean star;
    private List<UUID> tagIds;
    private String orderBy;
    private String orderDirection;
}
