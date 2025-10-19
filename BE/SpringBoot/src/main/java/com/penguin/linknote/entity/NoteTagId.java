package com.penguin.linknote.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteTagId implements Serializable {
    
    @Column(name = "note_id")
    private UUID noteId;
    
    @Column(name = "tag_id")
    private UUID tagId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteTagId)) return false;
        NoteTagId that = (NoteTagId) o;
        return Objects.equals(noteId, that.noteId) && 
               Objects.equals(tagId, that.tagId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(noteId, tagId);
    }
}