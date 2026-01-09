package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.AccessPolicies;

public class AccessPoliciesRowMapper implements RowMapper<AccessPolicies> {
    @Override
    public AccessPolicies mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccessPolicies policy = new AccessPolicies();
        policy.setId(rs.getInt("id"));
        policy.setTitle(rs.getString("title"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            policy.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            policy.setUpdatedAt(updatedAt.toInstant());
        }

        return policy;
    }
}
