package com.penguin.linknote.repository.impl;

import com.penguin.linknote.entity.NoteInteraction;
import com.penguin.linknote.repository.NoteInteractionRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class NoteInteractionRepositoryImpl implements NoteInteractionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NoteInteractionRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int record(NoteInteraction interaction) {
        String sql = """
            INSERT INTO note_interactions (user_id, note_id, action, acted_at)
            VALUES (:userId, :noteId, :action, :actedAt)
            ON CONFLICT (user_id, note_id, action, date_trunc('minute', acted_at)) DO NOTHING
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("userId", interaction.getUserId())
            .addValue("noteId", interaction.getNoteId())
            .addValue("action", interaction.getAction().name())
            .addValue("actedAt", Timestamp.from(interaction.getActedAt()));

        return jdbcTemplate.update(sql, params);
    }
}
