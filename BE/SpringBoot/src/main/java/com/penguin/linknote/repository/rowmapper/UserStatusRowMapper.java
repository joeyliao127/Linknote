package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.UserStatusCode;

public class UserStatusRowMapper implements RowMapper<UserStatusCode> {
    @Override
    public UserStatusCode mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserStatusCode status = new UserStatusCode();
        status.setId(rs.getLong("id"));
        status.setTitle(rs.getString("title"));
        return status;
    }
}
