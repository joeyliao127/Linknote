package com.penguin.linknote.entity;

import lombok.Data;

import java.time.Instant;


@Data
public class NoteTag {
    
    private NoteTagId id;
    
    private Note note;
    
    private Tag tag;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
