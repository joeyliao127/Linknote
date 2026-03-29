package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.RagNote;
import com.penguin.linknote.repository.RagNoteRepository;
import com.penguin.linknote.repository.rowmapper.RagNoteRowMapper;

@Repository
public class RagNoteRepositoryImpl implements RagNoteRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RagNoteRowMapper rowMapper;

    public RagNoteRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RagNoteRowMapper();
    }

    @Override
    public List<RagNote> findByNotebookId(UUID notebookId, UUID userId) {
        String sql = """
            SELECT note_id, user_id, notebook_id, note_updated_at, created_at
            FROM rag_notes
            WHERE notebook_id = :notebookId AND user_id = :userId
            ORDER BY created_at DESC
            """;
        return jdbcTemplate.query(sql, Map.of("notebookId", notebookId, "userId", userId), rowMapper);
    }

    @Override
    public RagNote upsert(RagNote ragNote) {
        String sql = """
            INSERT INTO rag_notes (note_id, user_id, notebook_id, note_updated_at, created_at)
            VALUES (:noteId, :userId, :notebookId, :noteUpdatedAt, :createdAt)
            ON CONFLICT (note_id) DO UPDATE SET
                note_updated_at = EXCLUDED.note_updated_at,
                user_id         = EXCLUDED.user_id,
                notebook_id     = EXCLUDED.notebook_id
            RETURNING note_id, user_id, notebook_id, note_updated_at, created_at
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("noteId", ragNote.getNoteId())
            .addValue("userId", ragNote.getUserId())
            .addValue("notebookId", ragNote.getNotebookId())
            .addValue("noteUpdatedAt", toTimestamp(ragNote.getNoteUpdatedAt()))
            .addValue("createdAt", toTimestamp(ragNote.getCreatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID noteId, UUID userId) {
        String sql = "DELETE FROM rag_notes WHERE note_id = :noteId AND user_id = :userId";
        jdbcTemplate.update(sql, Map.of("noteId", noteId, "userId", userId));
    }

    private Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
}
