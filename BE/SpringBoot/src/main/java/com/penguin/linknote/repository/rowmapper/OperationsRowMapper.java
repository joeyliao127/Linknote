package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.Operations;

public class OperationsRowMapper implements RowMapper<Operations> {
    @Override
    public Operations mapRow(ResultSet rs, int rowNum) throws SQLException {
        Operations operation = new Operations();
        operation.setId(rs.getInt("id"));
        operation.setTitle(rs.getString("title"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            operation.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            operation.setUpdatedAt(updatedAt.toInstant());
        }

        return operation;
    }
}
