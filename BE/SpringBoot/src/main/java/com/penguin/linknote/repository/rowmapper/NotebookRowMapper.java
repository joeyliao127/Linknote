package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.Notebook;

public class NotebookRowMapper implements RowMapper<Notebook> {
    @Override
    public Notebook mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notebook notebook = new Notebook();
        notebook.setId(rs.getObject("id", java.util.UUID.class));
        notebook.setTitle(rs.getString("title"));
        notebook.setDescription(rs.getString("description"));

        Boolean isActive = rs.getObject("is_active", Boolean.class);
        notebook.setIsActive(isActive);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notebook.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            notebook.setUpdatedAt(updatedAt.toInstant());
        }

        return notebook;
    }
}
