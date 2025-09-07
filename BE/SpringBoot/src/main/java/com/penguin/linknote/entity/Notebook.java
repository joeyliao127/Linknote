package com.penguin.linknote.entity;

import com.penguin.linknote.domain.notebook.NotebookDTO;
import jakarta.persistence.*;
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

    @Column(name="created_at")
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UUID getUserId() {
        return user.getId();
    }

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
