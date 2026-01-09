package com.penguin.linknote.repository.impl;

import java.util.UUID;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.repository.PermissionQueryRepository;

@Repository
@Transactional
public class PermissionQueryRepositoryImpl implements PermissionQueryRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public PermissionQueryRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * UUID resourceInstanceId like notebookId, noteId, tagId etc.
	 * DB 層無法使用 fk 關聯 resourceInstanceId
	 */
	@Override
	public boolean hasPermission(UUID userId, UUID resourceInstanceId,
								OperationType operation, ResourceType resourceType) {

		String sql = """
			SELECT 1
			FROM resource_acl ura
			JOIN roles r ON ura.role_id = r.id
			JOIN role_permissions rp ON rp.role_id = r.id
			JOIN resources rs ON rp.resource_id = rs.id
			JOIN operations op ON rp.operation_id = op.id
			WHERE ura.user_id = :userId
			AND ura.resource_instance_id = :resourceInstanceId
			AND rs.title = :resourceType
			AND op.title = :operation
			LIMIT 1
			""";

		var params = new java.util.HashMap<String, Object>();
		params.put("userId", userId);
		params.put("resourceInstanceId", resourceInstanceId);
		params.put("resourceType", resourceType.name());
		params.put("operation", operation.name());

		var results = jdbcTemplate.query(sql, params, (rs, rowNum) -> 1);

		return !results.isEmpty();
	}

	@Override
	public void addResourceRolePermission(UUID userId, UUID resourceInstanceId, RoleType roleType, ResourceType resourceType) {
		String roleSql = "SELECT id FROM roles WHERE title = :roleType LIMIT 1";
		Integer roleId = jdbcTemplate.queryForObject(
				roleSql,
				java.util.Map.of("roleType", roleType.name()),
				Integer.class);
		
		String resourceSql = "SELECT id FROM resources WHERE title = :resourceType LIMIT 1";
		Integer resourceId = jdbcTemplate.queryForObject(
				resourceSql,
				java.util.Map.of("resourceType", resourceType.name()),
				Integer.class);
		
		String insertSQL = """
			INSERT INTO resource_acl (user_id, role_id, resource_id, resource_instance_id)
			VALUES (:userId, :roleId, :resourceId, :resourceInstanceId)
			""";
		jdbcTemplate.update(insertSQL, java.util.Map.of(
				"userId", userId,
				"roleId", roleId,
				"resourceId", resourceId,
				"resourceInstanceId", resourceInstanceId
		));
	}
	
}
