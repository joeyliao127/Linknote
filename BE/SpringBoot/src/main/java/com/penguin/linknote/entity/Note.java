package com.penguin.linknote.entity;

import com.penguin.linknote.domain.note.NoteDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="notes")
@Data
public class Note {
    @Id
    private UUID id;

    @Column(name="title")
    private String title;

    @Column(name="question")
    private String question;

    @Column(name="content")
    private String content;

    @Column(name="keypoint")
    private String keypoint;

    @Column(name="star")
    private Boolean star;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    @Column(name = "notebook_id")
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
