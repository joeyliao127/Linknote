package com.penguin.linknote.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.UserStatusCode;
import com.penguin.linknote.repository.UserStatusRepository;
import com.penguin.linknote.repository.rowmapper.UserStatusRowMapper;

@Repository
public class UserStatusRepositoryImpl implements UserStatusRepository {
    private static final String BASE_SELECT = """
        SELECT id, title
        FROM user_status_codes
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserStatusRowMapper rowMapper;

    public UserStatusRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new UserStatusRowMapper();
    }

    @Override
    public List<UserStatusCode> index(Integer limit) {
        if (limit == null || limit <= 0) {
            String sql = BASE_SELECT + " ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper);
        }
        String sql = BASE_SELECT + " ORDER BY id ASC LIMIT :limit";
        return jdbcTemplate.query(sql, java.util.Map.of("limit", limit), rowMapper);
    }

    @Override
    public Optional<UserStatusCode> get(Long id) {
        String sql = BASE_SELECT + " WHERE id = :id";
        List<UserStatusCode> result = jdbcTemplate.query(sql, java.util.Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public UserStatusCode create(UserStatusCode status) {
        String sql = """
            INSERT INTO user_status_codes (title)
            VALUES (:title)
            RETURNING id, title
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("title", status.getTitle());

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public UserStatusCode update(UserStatusCode status) {
        String sql = """
            UPDATE user_status_codes
            SET title = :title
            WHERE id = :id
            RETURNING id, title
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", status.getId())
            .addValue("title", status.getTitle());

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM user_status_codes WHERE id = :id";
        jdbcTemplate.update(sql, java.util.Map.of("id", id));
    }
}
