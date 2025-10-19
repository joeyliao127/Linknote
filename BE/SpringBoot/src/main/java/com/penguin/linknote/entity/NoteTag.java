package com.penguin.linknote.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@Entity
@Table(name = "note_tags")
@Data
public class NoteTag {
    
    @EmbeddedId
    private NoteTagId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("noteId")  // 映射到複合鍵的 noteId
    @JoinColumn(name = "note_id")
    private Note note;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")   // 映射到複合鍵的 tagId
    @JoinColumn(name = "tag_id")
    private Tag tag;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}