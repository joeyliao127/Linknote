package com.penguin.linknote.repository;

import java.util.UUID;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;

public interface PermissionQueryRepository {

	boolean hasPermission(UUID userId, UUID resource_instance_id, OperationType operation, ResourceType resourceType);
	
	void addResourceRolePermission(UUID userId, UUID resourceInstanceId, RoleType roleType, ResourceType resourceType);
	
} 