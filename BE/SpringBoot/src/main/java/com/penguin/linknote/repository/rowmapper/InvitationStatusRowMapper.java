package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.InvitationStatusCode;

public class InvitationStatusRowMapper implements RowMapper<InvitationStatusCode> {
    @Override
    public InvitationStatusCode mapRow(ResultSet rs, int rowNum) throws SQLException {
        InvitationStatusCode status = new InvitationStatusCode();
        status.setId(rs.getLong("id"));
        status.setTitle(rs.getString("title"));

        return status;
    }
}
