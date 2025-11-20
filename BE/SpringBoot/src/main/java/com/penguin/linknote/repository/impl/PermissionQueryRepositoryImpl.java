package com.penguin.linknote.repository.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.repository.PermissionQueryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PermissionQueryRepositoryImpl implements PermissionQueryRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean hasPermission(UUID userId, UUID resourceInstanceId,
								OperationType operation, ResourceType resourceType) {

		String sql = """
			SELECT 1
			FROM user_resource_acl ura
			JOIN roles r ON ura.role_id = r.id
			JOIN role_permission rp ON rp.role_id = r.id
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
	
}
