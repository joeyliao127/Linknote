package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.entity.User;
import com.penguin.linknote.repository.UserRepository;
import com.penguin.linknote.repository.rowmapper.UserRowMapper;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String BASE_SELECT = """
        SELECT u.id, u.username, u.email, u.password, u.user_status_id, u.created_at, u.updated_at
        FROM users u
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new UserRowMapper();
    }

    @Override
    public Optional<User> get(UUID id) {
        String sql = BASE_SELECT + " WHERE u.id = :id";
        List<User> result = jdbcTemplate.query(sql, java.util.Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public User create(User user) {
        String sql = """
            INSERT INTO users (id, username, email, password, user_status_id, created_at, updated_at)
            VALUES (:id, :username, :email, :password, :userStatusId, :createdAt, :updatedAt)
            RETURNING id, username, email, password, user_status_id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", user.getId())
            .addValue("username", user.getUsername())
            .addValue("email", user.getEmail())
            .addValue("password", user.getPassword())
            .addValue("userStatusId", user.getUserStatusId())
            .addValue("createdAt", toTimestamp(user.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(user.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public User update(User user) {
        String sql = """
            UPDATE users
            SET username = :username,
                email = :email,
                password = :password,
                user_status_id = :userStatusId,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, username, email, password, user_status_id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", user.getId())
            .addValue("username", user.getUsername())
            .addValue("email", user.getEmail())
            .addValue("password", user.getPassword())
            .addValue("userStatusId", user.getUserStatusId())
            .addValue("updatedAt", toTimestamp(user.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM users WHERE id = :id";
        jdbcTemplate.update(sql, java.util.Map.of("id", id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = BASE_SELECT + " WHERE u.email = :email";
        List<User> result = jdbcTemplate.query(sql, java.util.Map.of("email", email), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<User> findAllByUsername(String title) {
        String sql = BASE_SELECT + " WHERE u.username = :title";
        return jdbcTemplate.query(sql, java.util.Map.of("title", title), rowMapper);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = BASE_SELECT + " WHERE u.email = :email AND u.password = :password";
        List<User> result = jdbcTemplate.query(sql, java.util.Map.of("email", email, "password", password), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
