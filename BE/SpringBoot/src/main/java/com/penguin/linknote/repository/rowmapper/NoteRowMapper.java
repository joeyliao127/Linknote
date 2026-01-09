package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.Note;

public class NoteRowMapper implements RowMapper<Note> {
    @Override
    public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
        Note note = new Note();
        note.setId(rs.getObject("id", java.util.UUID.class));
        note.setNotebookId(rs.getObject("notebook_id", java.util.UUID.class));
        note.setTitle(rs.getString("title"));
        note.setQuestion(rs.getString("question"));
        note.setContent(rs.getString("content"));
        note.setKeypoint(rs.getString("keypoint"));
        note.setStar(rs.getObject("star", Boolean.class));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            note.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            note.setUpdatedAt(updatedAt.toInstant());
        }

        return note;
    }
}
