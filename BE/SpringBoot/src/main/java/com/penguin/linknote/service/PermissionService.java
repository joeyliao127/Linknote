package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;

public interface PermissionService {
	boolean hasPermission(UUID userId, UUID resource_instance_id, OperationType operation, ResourceType resource);
	void addResourceRolePermission(UUID roleId, UUID resourceInstanceId, RoleType roleType, ResourceType resourceType);
}