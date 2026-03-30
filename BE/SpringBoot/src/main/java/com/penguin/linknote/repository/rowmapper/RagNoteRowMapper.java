package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.RagNote;

public class RagNoteRowMapper implements RowMapper<RagNote> {
    @Override
    public RagNote mapRow(ResultSet rs, int rowNum) throws SQLException {
        RagNote ragNote = new RagNote();
        ragNote.setNoteId(rs.getObject("note_id", UUID.class));
        ragNote.setUserId(rs.getObject("user_id", UUID.class));
        ragNote.setNotebookId(rs.getObject("notebook_id", UUID.class));
        ragNote.setNoteUpdatedAt(rs.getTimestamp("note_updated_at").toInstant());
        ragNote.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return ragNote;
    }
}
