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
import com.penguin.linknote.domain.invitation.InvitationCondition;
import com.penguin.linknote.domain.invitation.InvitationOrderBy;
import com.penguin.linknote.domain.invitation.exception.InvalidInvitationParameterException;
import com.penguin.linknote.entity.Invitation;
import com.penguin.linknote.repository.InvitationRepository;
import com.penguin.linknote.repository.rowmapper.InvitationRowMapper;

@Repository
public class InvitationRepositoryImpl implements InvitationRepository {
    private static final String BASE_SELECT = """
        SELECT i.id, i.inviter_id, i.invitee_id, i.message, i.created_at, i.updated_at, i.notebook_id,
               u.username AS invitee_username, u.email AS invitee_email,
               s.id AS status_id, s.title AS status_title
        FROM invitations i
        JOIN users u ON i.invitee_id = u.id
        JOIN invitation_status_codes s ON i.invitation_status_id = s.id
        """;

    private static final String BASE_COUNT = """
        SELECT COUNT(*)
        FROM invitations i
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final InvitationRowMapper rowMapper;

    @Value("${maxPageSize}")
    private Integer maxPageSize;

    @Value("${defaultPageSize}")
    private Integer defaultPageSize;

    public InvitationRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new InvitationRowMapper();
    }

    @Override
    public List<Invitation> indexByInviter(UUID userId, Integer limit) {
        int normalizedLimit = normalizeLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", normalizedLimit);

        String sql = BASE_SELECT + """
            WHERE i.inviter_id = :userId
            ORDER BY i.updated_at DESC
            LIMIT :limit
            """;

        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public List<Invitation> indexByInvitee(UUID userId, Integer limit) {
        int normalizedLimit = normalizeLimit(limit);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", normalizedLimit);

        String sql = BASE_SELECT + """
            WHERE i.invitee_id = :userId
            ORDER BY i.updated_at DESC
            LIMIT :limit
            """;

        return jdbcTemplate.query(sql, params, rowMapper);
    }

    @Override
    public PageResponse<Invitation> paginateByInviter(int page, int limit, InvitationCondition condition) {
        return paginateByUser(page, limit, condition, true);
    }

    @Override
    public PageResponse<Invitation> paginateByInvitee(int page, int limit, InvitationCondition condition) {
        return paginateByUser(page, limit, condition, false);
    }

    @Override
    public Optional<Invitation> get(UUID id) {
        String sql = BASE_SELECT + " WHERE i.id = :id";
        List<Invitation> result = jdbcTemplate.query(sql, Map.of("id", id), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Optional<Invitation> getByInvitee(UUID id, UUID inviteeId) {
        String sql = BASE_SELECT + """
            WHERE i.id = :id
              AND i.invitee_id = :inviteeId
            """;

        List<Invitation> result = jdbcTemplate.query(sql, Map.of("id", id, "inviteeId", inviteeId), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Optional<Invitation> getByInviterAndInvitee(UUID inviterId, UUID inviteeId) {
        String sql = BASE_SELECT + """
            WHERE i.inviter_id = :inviterId
              AND i.invitee_id = :inviteeId
            """;

        List<Invitation> result = jdbcTemplate.query(sql, Map.of("inviterId", inviterId, "inviteeId", inviteeId), rowMapper);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public Invitation create(Invitation invitation) {
        String sql = """
            INSERT INTO invitations (id, inviter_id, invitee_id, message, invitation_status_id, created_at, updated_at, notebook_id)
            VALUES (:id, :inviterId, :inviteeId, :message, :statusId, :createdAt, :updatedAt, :notebookId)
            RETURNING id
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", invitation.getId())
            .addValue("inviterId", invitation.getInviterId())
            .addValue("inviteeId", invitation.getInvitee().getId())
            .addValue("message", invitation.getMessage())
            .addValue("statusId", invitation.getInvitationStatusCode().getId())
            .addValue("createdAt", toTimestamp(invitation.getCreatedAt()))
            .addValue("updatedAt", toTimestamp(invitation.getUpdatedAt()))
            .addValue("notebookId", invitation.getNotebookId());

        UUID id = jdbcTemplate.queryForObject(sql, params, UUID.class);
        return get(id).orElseThrow();
    }

    @Override
    public Invitation update(Invitation invitation) {
        String sql = """
            UPDATE invitations
            SET message = :message,
                invitation_status_id = :statusId,
                updated_at = :updatedAt
            WHERE id = :id
            RETURNING id
            """;

        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", invitation.getId())
            .addValue("message", invitation.getMessage())
            .addValue("statusId", invitation.getInvitationStatusCode().getId())
            .addValue("updatedAt", toTimestamp(invitation.getUpdatedAt()));

        UUID id = jdbcTemplate.queryForObject(sql, params, UUID.class);
        return get(id).orElseThrow();
    }

    @Override
    public void delete(UUID id) {
        String sql = "DELETE FROM invitations WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private PageResponse<Invitation> paginateByUser(int page, int limit, InvitationCondition condition, boolean inviter) {
        int normalizedPage = normalizePage(page);
        int normalizedLimit = normalizeLimit(limit);

        Map<String, Object> params = new HashMap<>();
        List<String> conditions = buildConditions(condition, params, inviter);

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

        List<Invitation> items = jdbcTemplate.query(sql, params, rowMapper);

        PageResponse<Invitation> response = new PageResponse<>();
        response.setItems(items);
        response.setCount(total);
        response.setCurrentPage(normalizedPage);
        response.setPageSize(normalizedLimit);
        response.setTotalPage(totalPage);
        return response;
    }

    private List<String> buildConditions(InvitationCondition condition, Map<String, Object> params, boolean inviter) {
        if (condition == null || condition.getUserId() == null) {
            throw new InvalidInvitationParameterException();
        }

        List<String> conditions = new ArrayList<>();
        if (inviter) {
            conditions.add("i.inviter_id = :userId");
        } else {
            conditions.add("i.invitee_id = :userId");
        }
        params.put("userId", condition.getUserId());
        return conditions;
    }

    private String buildOrderBy(InvitationCondition condition) {
        String orderBy = condition == null ? null : condition.getOrderBy();
        String direction = condition == null ? null : condition.getOrderDirection();

        String column = "i.updated_at";
        if (orderBy != null && !orderBy.isBlank()) {
            InvitationOrderBy orderByEnum = InvitationOrderBy.from(orderBy);
            if (orderByEnum == null) {
                throw new InvalidInvitationParameterException();
            }
            column = "i." + orderByEnum.getColumn();
        }

        String normalizedDirection = "desc";
        if (direction != null && !direction.isBlank()) {
            String normalized = direction.toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new InvalidInvitationParameterException();
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
