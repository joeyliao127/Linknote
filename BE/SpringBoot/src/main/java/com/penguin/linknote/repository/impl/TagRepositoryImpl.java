package com.penguin.linknote.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCondition;
import com.penguin.linknote.domain.tag.TagOrderBy;
import com.penguin.linknote.domain.tag.exception.InvalidTagParameterException;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.repository.rowmapper.TagRowMapper;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private static final String BASE_SELECT = """
        SELECT t.id, t.title, t.user_id, t.created_at, t.updated_at
        FROM tags t
        """;

    private static final String BASE_COUNT = """
        SELECT COUNT(*)
        FROM tags t
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TagRowMapper rowMapper;

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    public TagRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new TagRowMapper();
    }

    @Override
    public List<Tag> index(UUID userId, Integer limit) {
        int normalizedLimit = normalizeLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", normalizedLimit);

        String sql = BASE_SELECT + """
            WHERE t.user_id = :userId
            ORDER BY t.updated_at DESC
            LIMIT :limit
            """;

        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public PageResponse<Tag> paginate(int page, int limit, TagCondition condition) {
        int normalizedPage = normalizePage(page);
        int normalizedLimit = normalizeLimit(limit);

        Map<String, Object> params = new HashMap<>();
        List<String> conditions = buildConditions(condition, params);

        String whereClause = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
        Integer total = jdbcTemplate.queryForObject(BASE_COUNT + whereClause, params, Integer.class);
        if (total == null) {
            total = 0;
        }

        int totalPage = calculateTotalPage(total, normalizedLimit);

        params.put("limit", normalizedLimit);
        params.put("offset", (normalizedPage - 1) * normalizedLimit);

        String sql = BASE_SELECT + whereClause + "\n" + buildOrderBy(condition)
            + "\nLIMIT :limit\nOFFSET :offset";

        List<Tag> items = jdbcTemplate.query(sql, params, rowMapper);

        PageResponse<Tag> response = new PageResponse<>();
        response.setItems(items);
        response.setCount(total);
        response.setCurrentPage(normalizedPage);
        response.setPageSize(normalizedLimit);
        response.setTotalPage(totalPage);
        return response;
    }

    @Override
    public Optional<Tag> get(UUID id) {
        String sql = BASE_SELECT + " WHERE t.id = :id";
        List<Tag> result = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Tag create(Tag tag) {
        String sql = """
            INSERT INTO tags (id, title, user_id, created_at, updated_at)
            VALUES (:id, :title, :userId, :createdAt, :updatedAt)
            RETURNING id, title, user_id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", tag.getId())
            .addValue("title", tag.getTitle())
            .addValue("userId", tag.getUserId())
            .addValue("createdAt", toTimestamp(tag.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(tag.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public Tag update(Tag tag) {
        String sql = """
            UPDATE tags
            SET title = :title,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, title, user_id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", tag.getId())
            .addValue("title", tag.getTitle())
            .addValue("updatedAt", toTimestamp(tag.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM tags WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    @Override
    public List<Tag> findAllById(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String sql = BASE_SELECT + " WHERE t.id IN (:ids)";
        return jdbcTemplate.query(sql, Map.of("ids", ids), rowMapper);
    }

    private List<String> buildConditions(TagCondition condition, Map<String, Object> params) {
        if (condition == null || condition.getUserId() == null) {
            throw new InvalidTagParameterException();
        }

        List<String> conditions = new ArrayList<>();
        conditions.add("t.user_id = :userId");
        params.put("userId", condition.getUserId());

        if (condition.getTitle() != null && !condition.getTitle().isBlank()) {
            conditions.add("t.title ILIKE :title");
            params.put("title", condition.getTitle());
        }

        return conditions;
    }

    private String buildOrderBy(TagCondition condition) {
        String orderBy = condition == null ? null : condition.getOrderBy();
        String direction = condition == null ? null : condition.getOrderDirection();

        String column = "t.updated_at";
        if (orderBy != null && !orderBy.isBlank()) {
            TagOrderBy orderByEnum = TagOrderBy.from(orderBy);
            if (orderByEnum == null) {
                throw new InvalidTagParameterException();
            }
            column = "t." + orderByEnum.getColumn();
        }

        String normalizedDirection = "desc";
        if (direction != null && !direction.isBlank()) {
            String normalized = direction.toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new InvalidTagParameterException();
            }
            normalizedDirection = normalized;
        }

        return "ORDER BY " + column + " " + normalizedDirection;
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return defaultPageSize;
        }
        return Math.min(limit, maxPageSize);
    }

    private int calculateTotalPage(int total, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
