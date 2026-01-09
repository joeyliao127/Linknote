package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.Resources;

public class ResourcesRowMapper implements RowMapper<Resources> {
    @Override
    public Resources mapRow(ResultSet rs, int rowNum) throws SQLException {
        Resources resource = new Resources();
        resource.setId(rs.getInt("id"));
        resource.setTitle(rs.getString("title"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            resource.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            resource.setUpdatedAt(updatedAt.toInstant());
        }

        return resource;
    }
}
