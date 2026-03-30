package com.penguin.linknote.domain.ragnote;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.penguin.linknote.entity.RagNote;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RagNoteDTO {
    private UUID noteId;
    private UUID userId;
    private UUID notebookId;
    private Instant noteUpdatedAt;
    private Instant createdAt;

    public static RagNoteDTO fromEntity(RagNote ragNote) {
        return RagNoteDTO.builder()
                .noteId(ragNote.getNoteId())
                .userId(ragNote.getUserId())
                .notebookId(ragNote.getNotebookId())
                .noteUpdatedAt(ragNote.getNoteUpdatedAt())
                .createdAt(ragNote.getCreatedAt())
                .build();
    }

    public static List<RagNoteDTO> fromEntityList(List<RagNote> list) {
        return list.stream().map(RagNoteDTO::fromEntity).toList();
    }
}
