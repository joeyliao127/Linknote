package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.AccessPolicies;
import com.penguin.linknote.repository.AccessPoliciesRepository;
import com.penguin.linknote.repository.rowmapper.AccessPoliciesRowMapper;

@Repository
public class AccessPoliciesRepositoryImpl implements AccessPoliciesRepository {
    private static final String BASE_SELECT = """
        SELECT id, title, created_at, updated_at
        FROM access_policies
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AccessPoliciesRowMapper rowMapper;

    public AccessPoliciesRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new AccessPoliciesRowMapper();
    }

    @Override
    public List<AccessPolicies> index(Integer limit) {
        if (limit == null || limit <= 0) {
            String sql = BASE_SELECT + " ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper);
        }
        String sql = BASE_SELECT + " ORDER BY id ASC LIMIT :limit";
        return jdbcTemplate.query(sql, java.util.Map.of("limit", limit), rowMapper);
    }

    @Override
    public Optional<AccessPolicies> get(Integer id) {
        String sql = BASE_SELECT + " WHERE id = :id";
        List<AccessPolicies> result = jdbcTemplate.query(sql, java.util.Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public AccessPolicies create(AccessPolicies policy) {
        String sql = """
            INSERT INTO access_policies (title, created_at, updated_at)
            VALUES (:title, :createdAt, :updatedAt)
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", policy.getTitle())
            .addValue("createdAt", toTimestamp(policy.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(policy.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public AccessPolicies update(AccessPolicies policy) {
        String sql = """
            UPDATE access_policies
            SET title = :title,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, title, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", policy.getId())
            .addValue("title", policy.getTitle())
            .addValue("updatedAt", toTimestamp(policy.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM access_policies WHERE id = :id";
        jdbcTemplate.update(sql, java.util.Map.of("id", id));
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
