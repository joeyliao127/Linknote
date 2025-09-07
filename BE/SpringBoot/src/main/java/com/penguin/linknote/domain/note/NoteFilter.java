package com.penguin.linknote.domain.note;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class NoteFilter {
    private UUID notebookId;
    private String title;
    private Boolean star;
    private List<UUID> tagIdList;
}
