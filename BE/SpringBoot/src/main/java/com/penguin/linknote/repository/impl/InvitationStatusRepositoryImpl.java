package com.penguin.linknote.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.InvitationStatusCode;
import com.penguin.linknote.repository.InvitationStatusRepository;
import com.penguin.linknote.repository.rowmapper.InvitationStatusRowMapper;

@Repository
public class InvitationStatusRepositoryImpl implements InvitationStatusRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final InvitationStatusRowMapper rowMapper;

    public InvitationStatusRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new InvitationStatusRowMapper();
    }

    @Override
    public Optional<InvitationStatusCode> findByTitle(String title) {
        String sql = """
            SELECT id, title
            FROM invitation_status_codes
            WHERE title = :title
            LIMIT 1
            """;

        List<InvitationStatusCode> result = jdbcTemplate.query(sql, java.util.Map.of("title", title), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }
}
