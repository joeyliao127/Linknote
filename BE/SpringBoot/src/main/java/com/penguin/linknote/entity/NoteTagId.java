package com.penguin.linknote.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteTagId implements Serializable {
    
    private UUID noteId;
    
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
