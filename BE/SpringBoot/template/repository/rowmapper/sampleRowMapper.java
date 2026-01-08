package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.!{upper};

public class !{upper}RowMapper implements RowMapper<!{upper}> {
    @Override
    public !{upper} mapRow(ResultSet rs, int rowNum) throws SQLException {
        !{upper} entity = new !{upper}();
        entity.setId(rs.getObject("id", java.util.UUID.class));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            entity.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            entity.setUpdatedAt(updatedAt.toInstant());
        }

        // TODO: map other fields

        return entity;
    }
}
