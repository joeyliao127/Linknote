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
import com.penguin.linknote.domain.note.NoteCondition;
import com.penguin.linknote.domain.note.NoteOrderBy;
import com.penguin.linknote.domain.note.exception.InvalidNoteParameterException;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.entity.Note;
import com.penguin.linknote.repository.NoteRepository;
import com.penguin.linknote.repository.rowmapper.NoteRowMapper;

@Repository
public class NoteRepositoryImpl implements NoteRepository {
    private static final String BASE_SELECT = """
        SELECT n.id, n.notebook_id, n.title, n.question, n.content, n.keypoint, n.star,
               n.created_at, n.updated_at
        FROM notes n
        JOIN resource_acl ra ON ra.resource_instance_id = n.notebook_id
        JOIN resources rs ON ra.resource_id = rs.id
        JOIN roles r ON ra.role_id = r.id
        """;

    private static final String BASE_COUNT = """
        SELECT COUNT(*)
        FROM notes n
        JOIN resource_acl ra ON ra.resource_instance_id = n.notebook_id
        JOIN resources rs ON ra.resource_id = rs.id
        JOIN roles r ON ra.role_id = r.id
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final NoteRowMapper rowMapper;

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    public NoteRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new NoteRowMapper();
    }

    @Override
    public List<Note> index(UUID userId, UUID notebookId, Integer limit) {
        int normalizedLimit = normalizeLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("resourceType", ResourceType.NOTEBOOK.name());
        params.put("roleType", RoleType.ROLE_OWNER.name());
        params.put("notebookId", notebookId);
        params.put("limit", normalizedLimit);

        String sql = BASE_SELECT + """
            WHERE ra.user_id = :userId
              AND rs.title = :resourceType
              AND r.title = :roleType
              AND n.notebook_id = :notebookId
            ORDER BY n.updated_at DESC
            LIMIT :limit
            """;

        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public PageResponse<Note> paginate(int page, int limit, NoteCondition condition) {
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

        List<Note> items = jdbcTemplate.query(sql, params, rowMapper);

        PageResponse<Note> response = new PageResponse<>();
        response.setItems(items);
        response.setCount(total);
        response.setCurrentPage(normalizedPage);
        response.setPageSize(normalizedLimit);
        response.setTotalPage(totalPage);
        return response;
    }

    @Override
    public Optional<Note> get(UUID id) {
        String sql = """
            SELECT n.id, n.notebook_id, n.title, n.question, n.content, n.keypoint, n.star,
                   n.created_at, n.updated_at
            FROM notes n
            WHERE n.id = :id
            """;

        List<Note> result = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Note create(Note note) {
        String sql = """
            INSERT INTO notes (id, notebook_id, title, question, content, keypoint, star, created_at, updated_at)
            VALUES (:id, :notebookId, :title, :question, :content, :keypoint, :star, :createdAt, :updatedAt)
            RETURNING id, notebook_id, title, question, content, keypoint, star, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", note.getId())
            .addValue("notebookId", note.getNotebookId())
            .addValue("title", note.getTitle())
            .addValue("question", note.getQuestion())
            .addValue("content", note.getContent())
            .addValue("keypoint", note.getKeypoint())
            .addValue("star", note.getStar())
            .addValue("createdAt", toTimestamp(note.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(note.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public Note update(Note note) {
        String sql = """
            UPDATE notes
            SET title = :title,
                question = :question,
                content = :content,
                keypoint = :keypoint,
                star = :star,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, notebook_id, title, question, content, keypoint, star, created_at, updated_at
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", note.getId())
            .addValue("title", note.getTitle())
            .addValue("question", note.getQuestion())
            .addValue("content", note.getContent())
            .addValue("keypoint", note.getKeypoint())
            .addValue("star", note.getStar())
            .addValue("updatedAt", toTimestamp(note.getUpdatedAt()));

        return jdbcTemplate.queryForObject(sql, params, rowMapper);
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM notes WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private List<String> buildConditions(NoteCondition condition, Map<String, Object> params) {
        if (condition == null || condition.getUserId() == null || condition.getNotebookId() == null) {
            throw new InvalidNoteParameterException();
        }

        List<String> conditions = new ArrayList<>();
        conditions.add("ra.user_id = :userId");
        conditions.add("rs.title = :resourceType");
        conditions.add("r.title = :roleType");
        conditions.add("n.notebook_id = :notebookId");

        params.put("userId", condition.getUserId());
        params.put("resourceType", ResourceType.NOTEBOOK.name());
        params.put("roleType", RoleType.ROLE_OWNER.name());
        params.put("notebookId", condition.getNotebookId());

        if (condition.getTitle() != null && !condition.getTitle().isBlank()) {
            conditions.add("n.title ILIKE :title");
            params.put("title", "%" + condition.getTitle() + "%");
        }
        if (condition.getStar() != null) {
            conditions.add("n.star = :star");
            params.put("star", condition.getStar());
        }
        if (condition.getTagIds() != null && !condition.getTagIds().isEmpty()) {
            conditions.add("""
                n.id IN (
                    SELECT nt.note_id
                    FROM note_tags nt
                    WHERE nt.tag_id IN (:tagIds)
                )
                """);
            params.put("tagIds", condition.getTagIds());
        }

        return conditions;
    }

    private String buildOrderBy(NoteCondition condition) {
        String orderBy = condition == null ? null : condition.getOrderBy();
        String direction = condition == null ? null : condition.getOrderDirection();

        String column = "n.updated_at";
        if (orderBy != null && !orderBy.isBlank()) {
            NoteOrderBy orderByEnum = NoteOrderBy.from(orderBy);
            if (orderByEnum == null) {
                throw new InvalidNoteParameterException();
            }
            column = "n." + orderByEnum.getColumn();
        }

        String normalizedDirection = "desc";
        if (direction != null && !direction.isBlank()) {
            String normalized = direction.toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new InvalidNoteParameterException();
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
