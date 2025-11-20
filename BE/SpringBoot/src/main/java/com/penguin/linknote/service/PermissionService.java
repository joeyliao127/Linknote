package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;

public interface PermissionService {
	boolean hasPermission(UUID userId, UUID resource_instance_id, OperationType operation, ResourceType resource);
}