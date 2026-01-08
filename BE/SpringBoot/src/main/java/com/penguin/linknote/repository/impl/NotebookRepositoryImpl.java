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
import com.penguin.linknote.domain.notebook.NotebookCondition;
import com.penguin.linknote.domain.notebook.NotebookOrderBy;
import com.penguin.linknote.domain.notebook.exception.InvalidNotebookParameterException;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.entity.Notebook;
import com.penguin.linknote.repository.NotebookRepository;
import com.penguin.linknote.repository.rowmapper.NotebookRowMapper;

@Repository
public class NotebookRepositoryImpl implements NotebookRepository {
    private static final String BASE_SELECT = """
        SELECT n.id, n.title, n.description, n.is_active, n.created_at, n.updated_at
        FROM notebooks n
        JOIN resource_acl ra ON ra.resource_instance_id = n.id
        JOIN resources rs ON ra.resource_id = rs.id
        JOIN roles r ON ra.role_id = r.id
        """;

    private static final String BASE_COUNT = """
        SELECT COUNT(*)
        FROM notebooks n
        JOIN resource_acl ra ON ra.resource_instance_id = n.id
        JOIN resources rs ON ra.resource_id = rs.id
        JOIN roles r ON ra.role_id = r.id
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NotebookRowMapper rowMapper;

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    public NotebookRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new NotebookRowMapper();
    }

    @Override
    public List<Notebook> index(UUID userId, Integer limit) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required for index");
        }

        int normalizedLimit = normalizeLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("resourceType", ResourceType.NOTEBOOK.name());
        params.put("roleType", RoleType.ROLE_OWNER.name());
        params.put("limit", normalizedLimit);

        String sql = BASE_SELECT + """
            WHERE ra.user_id = :userId
              AND rs.title = :resourceType
              AND r.title = :roleType
            ORDER BY n.updated_at DESC
            LIMIT :limit
            """;
        
        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public PageResponse<Notebook> paginate(int page, int limit, NotebookCondition condition) {
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

        List<Notebook> items = jdbcTemplate.query(sql, params, rowMapper);

        PageResponse<Notebook> response = new PageResponse<>();
        response.setItems(items);
        response.setCount(items.size());
        response.setCurrentPage(normalizedPage);
        response.setPageSize(normalizedLimit);
        response.setTotalPage(totalPage);
        return response;
    }

    @Override
    public Optional<Notebook> get(UUID id) {
        String sql = """
            SELECT id, title, description, is_active, created_at, updated_at
            FROM notebooks
            WHERE id = :id
            """;

        List<Notebook> result = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Notebook create(Notebook notebook) {
        String sql = """
            INSERT INTO notebooks (id, title, description, is_active, created_at, updated_at)
            VALUES (:id, :title, :description, :isActive, :createdAt, :updatedAt)
            RETURNING id, title, description, is_active, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", notebook.getId())
            .addValue("title", notebook.getTitle())
            .addValue("description", notebook.getDescription())
            .addValue("isActive", notebook.getIsActive())
            .addValue("createdAt", toTimestamp(notebook.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(notebook.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public Notebook update(Notebook notebook) {
        String sql = """
            UPDATE notebooks
            SET title = :title,
                description = :description,
                is_active = :isActive,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, title, description, is_active, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", notebook.getId())
            .addValue("title", notebook.getTitle())
            .addValue("description", notebook.getDescription())
            .addValue("isActive", notebook.getIsActive())
            .addValue("updatedAt", toTimestamp(notebook.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM notebooks WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private List<String> buildConditions(NotebookCondition condition, Map<String, Object> params) {
        if (condition == null || condition.getUserId() == null) {
            throw new IllegalArgumentException("userId is required for pagination");
        }

        List<String> conditions = new ArrayList<>();
        conditions.add("ra.user_id = :userId");
        conditions.add("rs.title = :resourceType");
        conditions.add("r.title = :roleType");

        params.put("userId", condition.getUserId());
        params.put("resourceType", ResourceType.NOTEBOOK.name());
        params.put("roleType", RoleType.ROLE_OWNER.name());

        if (condition.getTitle() != null) {
            conditions.add("n.title = :title");
            params.put("title", condition.getTitle());
        }
        if (condition.getActive() != null) {
            conditions.add("n.is_active = :active");
            params.put("active", condition.getActive());
        }

        return conditions;
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

    private String buildOrderBy(NotebookCondition condition) {
        String orderBy = condition == null ? null : condition.getOrderBy();
        String direction = condition == null ? null : condition.getOrderDirection();

        String column = "n.updated_at";
        if (orderBy != null && !orderBy.isBlank()) {
            NotebookOrderBy orderByEnum = NotebookOrderBy.from(orderBy);
            if (orderByEnum == null) {
                throw new InvalidNotebookParameterException();
            }
            column = orderByEnum.getColumn();
        }

        String normalizedDirection = "desc";
        if (direction != null && !direction.isBlank()) {
            String normalized = direction.toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new InvalidNotebookParameterException();
            }
            normalizedDirection = normalized;
        }

        return "ORDER BY " + column + " " + normalizedDirection;
    }
}
