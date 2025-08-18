package com.penguin.linknote.entity;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="notebooks")
@Data
public class Notebook {

    @Id
    private UUID id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="updated_at")
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
