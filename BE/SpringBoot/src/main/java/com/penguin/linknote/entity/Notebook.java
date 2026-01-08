package com.penguin.linknote.entity;

import java.time.Instant;
import java.util.UUID;

import com.penguin.linknote.domain.notebook.NotebookDTO;

import lombok.Data;

@Data
public class Notebook {

    private UUID id;

    private String title;

    private String description;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;

    public Notebook() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public NotebookDTO toDTO() {
        return NotebookDTO.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .active((this.isActive))
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
