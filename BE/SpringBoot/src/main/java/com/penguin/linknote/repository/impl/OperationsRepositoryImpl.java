package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.Operations;
import com.penguin.linknote.repository.OperationsRepository;
import com.penguin.linknote.repository.rowmapper.OperationsRowMapper;

@Repository
public class OperationsRepositoryImpl implements OperationsRepository {
    private static final String BASE_SELECT = """
        SELECT id, title, created_at, updated_at
        FROM operations
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OperationsRowMapper rowMapper;

    public OperationsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new OperationsRowMapper();
    }

    @Override
    public List<Operations> index(Integer limit) {
        if (limit == null || limit <= 0) {
            String sql = BASE_SELECT + " ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper);
        }
        String sql = BASE_SELECT + " ORDER BY id ASC LIMIT :limit";
        return jdbcTemplate.query(sql, java.util.Map.of("limit", limit), rowMapper);
    }

    @Override
    public Optional<Operations> get(Integer id) {
        String sql = BASE_SELECT + " WHERE id = :id";
        List<Operations> result = jdbcTemplate.query(sql, java.util.Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Operations create(Operations operation) {
        String sql = """
            INSERT INTO operations (title, created_at, updated_at)
            VALUES (:title, :createdAt, :updatedAt)
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", operation.getTitle())
            .addValue("createdAt", toTimestamp(operation.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(operation.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public Operations update(Operations operation) {
        String sql = """
            UPDATE operations
            SET title = :title,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", operation.getId())
            .addValue("title", operation.getTitle())
            .addValue("updatedAt", toTimestamp(operation.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM operations WHERE id = :id";
        jdbcTemplate.update(sql, java.util.Map.of("id", id));
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
