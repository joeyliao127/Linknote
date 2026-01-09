package com.penguin.linknote.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.penguin.linknote.entity.Invitation;
import com.penguin.linknote.entity.InvitationStatusCode;
import com.penguin.linknote.entity.User;

public class InvitationRowMapper implements RowMapper<Invitation> {
    @Override
    public Invitation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Invitation invitation = new Invitation();
        invitation.setId(rs.getObject("id", java.util.UUID.class));
        invitation.setInviterId(rs.getObject("inviter_id", java.util.UUID.class));
        invitation.setMessage(rs.getString("message"));
        invitation.setNotebookId(rs.getObject("notebook_id", java.util.UUID.class));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            invitation.setCreatedAt(createdAt.toInstant());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            invitation.setUpdatedAt(updatedAt.toInstant());
        }

        User invitee = new User();
        invitee.setId(rs.getObject("invitee_id", java.util.UUID.class));
        invitee.setUsername(rs.getString("invitee_username"));
        invitee.setEmail(rs.getString("invitee_email"));
        invitation.setInvitee(invitee);

        InvitationStatusCode status = new InvitationStatusCode();
        status.setId(rs.getLong("status_id"));
        status.setTitle(rs.getString("status_title"));
        invitation.setInvitationStatusCode(status);

        return invitation;
    }
}
