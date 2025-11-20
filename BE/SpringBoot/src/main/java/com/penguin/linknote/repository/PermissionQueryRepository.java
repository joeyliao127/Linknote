package com.penguin.linknote.repository;

import java.util.UUID;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;

public interface PermissionQueryRepository {

	boolean hasPermission(UUID userId, UUID resource_instance_id, OperationType operation, ResourceType resourceType);
	
} 