package com.penguin.linknote.entity;

import com.penguin.linknote.domain.note.NoteDTO;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Note {
    private UUID id;

    private String title;

    private String question;

    private String content;

    private String keypoint;

    private Boolean star;

    private Instant createdAt;

    private Instant updatedAt;

    private UUID notebookId;

    public Note() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public NoteDTO toDTO() {
        return NoteDTO.builder()
                .id(this.id)
                .title(this.title)
                .question(this.question)
                .content(this.content)
                .keypoint(this.keypoint)
                .star(this.star)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
