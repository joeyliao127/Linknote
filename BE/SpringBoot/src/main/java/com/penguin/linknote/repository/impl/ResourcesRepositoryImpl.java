package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.Resources;
import com.penguin.linknote.repository.ResourcesRepository;
import com.penguin.linknote.repository.rowmapper.ResourcesRowMapper;

@Repository
public class ResourcesRepositoryImpl implements ResourcesRepository {
    private static final String BASE_SELECT = """
        SELECT id, title, created_at, updated_at
        FROM resources
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ResourcesRowMapper rowMapper;

    public ResourcesRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new ResourcesRowMapper();
    }

    @Override
    public List<Resources> index(Integer limit) {
        if (limit == null || limit <= 0) {
            String sql = BASE_SELECT + " ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper);
        }
        String sql = BASE_SELECT + " ORDER BY id ASC LIMIT :limit";
        return jdbcTemplate.query(sql, java.util.Map.of("limit", limit), rowMapper);
    }

    @Override
    public Optional<Resources> get(Integer id) {
        String sql = BASE_SELECT + " WHERE id = :id";
        List<Resources> result = jdbcTemplate.query(sql, java.util.Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Resources create(Resources resource) {
        String sql = """
            INSERT INTO resources (title, created_at, updated_at)
            VALUES (:title, :createdAt, :updatedAt)
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", resource.getTitle())
            .addValue("createdAt", toTimestamp(resource.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(resource.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public Resources update(Resources resource) {
        String sql = """
            UPDATE resources
            SET title = :title,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", resource.getId())
            .addValue("title", resource.getTitle())
            .addValue("updatedAt", toTimestamp(resource.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM resources WHERE id = :id";
        jdbcTemplate.update(sql, java.util.Map.of("id", id));
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
