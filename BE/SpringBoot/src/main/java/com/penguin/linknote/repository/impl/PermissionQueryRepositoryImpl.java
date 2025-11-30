package com.penguin.linknote.repository.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.repository.PermissionQueryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class PermissionQueryRepositoryImpl implements PermissionQueryRepository {

	@PersistenceContext
	private EntityManager em;

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

		List<?> results = em.createNativeQuery(sql)
				.setParameter("userId", userId)
				.setParameter("resourceInstanceId", resourceInstanceId)
				.setParameter("resourceType", resourceType.name())
				.setParameter("operation", operation.name())
				.getResultList();

		return !results.isEmpty();
	}

	@Override
	public void addResourceRolePermission(UUID userId, UUID resourceInstanceId, RoleType roleType, ResourceType resourceType) {
		String roleSql = "SELECT id FROM roles WHERE title = :roleType LIMIT 1";
		Integer roleId = (Integer) em.createNativeQuery(roleSql)
				.setParameter("roleType", roleType.name())
				.getSingleResult();
		
		String resourceSql = "SELECT id FROM resources WHERE title = :resourceType LIMIT 1";
		Integer resourceId = (Integer) em.createNativeQuery(resourceSql)
				.setParameter("resourceType", resourceType.name())
				.getSingleResult();
		
		String insertSQL = """
			INSERT INTO resource_acl (user_id, role_id, resource_id, resource_instance_id)
			VALUES (:userId, :roleId, :resourceId, :resourceInstanceId)
			""";
		em.createNativeQuery(insertSQL)
			.setParameter("userId", userId)
			.setParameter("roleId", roleId)
			.setParameter("resourceId", resourceId)
			.setParameter("resourceInstanceId", resourceInstanceId)
			.executeUpdate();
	}
	
}
