package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.NoteTag;
import com.penguin.linknote.entity.NoteTagId;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.NoteTagsRepository;
import com.penguin.linknote.repository.rowmapper.TagRowMapper;

@Repository
public class NoteTagsRepositoryImpl implements NoteTagsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;

    public NoteTagsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = new TagRowMapper();
    }

    @Override
    public void deleteByNoteId(UUID noteId) {
        String sql = "DELETE FROM note_tags WHERE note_id = :noteId";
        jdbcTemplate.update(sql, Map.of("noteId", noteId));
    }

    @Override
    public void saveAll(List<NoteTag> noteTags) {
        if (noteTags == null || noteTags.isEmpty()) {
            return;
        }

        String sql = """
            INSERT INTO note_tags (note_id, tag_id, created_at, updated_at)
            VALUES (:noteId, :tagId, :createdAt, :updatedAt)
            """;

        List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (NoteTag noteTag : noteTags) {
            NoteTagId id = noteTag.getId();
            if (id == null) {
                continue;
            }

            MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("noteId", id.getNoteId())
                .addValue("tagId", id.getTagId())
                .addValue("createdAt", toTimestamp(noteTag.getCreatedAt()))
                .addValue("updatedAt", toTimestamp(noteTag.getUpdatedAt()));
            batchParams.add(params);
        }

        if (!batchParams.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
        }
    }

    @Override
    public List<Tag> findTagsByNoteId(UUID noteId) {
        String sql = """
            SELECT t.id, t.title, t.user_id, t.created_at, t.updated_at
            FROM note_tags nt
            JOIN tags t ON nt.tag_id = t.id
            WHERE nt.note_id = :noteId
            ORDER BY t.title ASC
            """;

        return jdbcTemplate.query(sql, Map.of("noteId", noteId), tagRowMapper);
    }

    @Override
    public Map<UUID, List<Tag>> findTagsByNoteIds(List<UUID> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
            SELECT nt.note_id, t.id, t.title, t.user_id, t.created_at, t.updated_at
            FROM note_tags nt
            JOIN tags t ON nt.tag_id = t.id
            WHERE nt.note_id IN (:noteIds)
            ORDER BY t.title ASC
            """;

        List<NoteTagRow> rows = jdbcTemplate.query(sql, Map.of("noteIds", noteIds), (rs, rowNum) -> {
            UUID noteId = rs.getObject("note_id", java.util.UUID.class);
            Tag tag = tagRowMapper.mapRow(rs, rowNum);
            return new NoteTagRow(noteId, tag);
        });

        Map<UUID, List<Tag>> result = new HashMap<>();
        for (NoteTagRow row : rows) {
            result.computeIfAbsent(row.noteId, key -> new ArrayList<>()).add(row.tag);
        }
        return result;
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }

    private static class NoteTagRow {
        private final UUID noteId;
        private final Tag tag;

        private NoteTagRow(UUID noteId, Tag tag) {
            this.noteId = noteId;
            this.tag = tag;
        }
    }
}
