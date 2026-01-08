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
import com.penguin.linknote.domain.!{lower}.!{upper}Condition;
import com.penguin.linknote.domain.!{lower}.!{upper}OrderBy;
import com.penguin.linknote.domain.!{lower}.exception.Invalid!{upper}ParameterException;
import com.penguin.linknote.entity.!{upper};
import com.penguin.linknote.repository.!{upper}Repository;
import com.penguin.linknote.repository.rowmapper.!{upper}RowMapper;

@Repository
public class !{upper}RepositoryImpl implements !{upper}Repository {
    private static final String BASE_SELECT = """
        SELECT t.id, t.created_at, t.updated_at
        FROM !{lower}s t
        """;

    private static final String BASE_COUNT = """
        SELECT COUNT(*)
        FROM !{lower}s t
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final !{upper}RowMapper rowMapper;

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    public !{upper}RepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new !{upper}RowMapper();
    }

    @Override
    public List<!{upper}> index(Integer limit) {
        int normalizedLimit = normalizeLimit(limit);
        String sql = BASE_SELECT + "ORDER BY t.updated_at DESC LIMIT :limit";
        return jdbcTemplate.query(sql, Map.of("limit", normalizedLimit), rowMapper);
    }

    @Override
    public PageResponse<!{upper}> paginate(int page, int limit, !{upper}Condition condition) {
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

        List<!{upper}> items = jdbcTemplate.query(sql, params, rowMapper);

        PageResponse<!{upper}> response = new PageResponse<>();
        response.setItems(items);
        response.setCount(items.size());
        response.setCurrentPage(normalizedPage);
        response.setPageSize(normalizedLimit);
        response.setTotalPage(totalPage);
        return response;
    }

    @Override
    public Optional<!{upper}> get(UUID id) {
        String sql = BASE_SELECT + " WHERE t.id = :id";
        List<!{upper}> result = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public !{upper} create(!{upper} entity) {
        String sql = """
            INSERT INTO !{lower}s (id, created_at, updated_at)
            VALUES (:id, :createdAt, :updatedAt)
            RETURNING id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", entity.getId())
            .addValue("createdAt", toTimestamp(entity.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(entity.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public !{upper} update(!{upper} entity) {
        String sql = """
            UPDATE !{lower}s
            SET updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", entity.getId())
            .addValue("updatedAt", toTimestamp(entity.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM !{lower}s WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private List<String> buildConditions(!{upper}Condition condition, Map<String, Object> params) {
        List<String> conditions = new ArrayList<>();

        // TODO: add conditions with named parameters
        // if (condition.getTitle() != null) {
        //     conditions.add("t.title = :title");
        //     params.put("title", condition.getTitle());
        // }

        return conditions;
    }

    private String buildOrderBy(!{upper}Condition condition) {
        String orderBy = condition == null ? null : condition.getOrderBy();
        String direction = condition == null ? null : condition.getOrderDirection();

        String column = "t.updated_at";
        if (orderBy != null && !orderBy.isBlank()) {
            !{upper}OrderBy orderByEnum = !{upper}OrderBy.from(orderBy);
            if (orderByEnum == null) {
                throw new Invalid!{upper}ParameterException();
            }
            column = "t." + orderByEnum.getColumn();
        }

        String normalizedDirection = "desc";
        if (direction != null && !direction.isBlank()) {
            String normalized = direction.toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new Invalid!{upper}ParameterException();
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
