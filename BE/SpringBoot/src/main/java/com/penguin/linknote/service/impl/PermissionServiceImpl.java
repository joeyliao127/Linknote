package com.penguin.linknote.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.domain.rbac.OperationType;
import com.penguin.linknote.domain.rbac.ResourceType;
import com.penguin.linknote.domain.rbac.RoleType;
import com.penguin.linknote.repository.PermissionQueryRepository;
import com.penguin.linknote.service.PermissionService;


@Service
public class PermissionServiceImpl implements PermissionService {

	private PermissionQueryRepository permissionRepository;

	public PermissionServiceImpl(PermissionQueryRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	@Override
	public boolean hasPermission(UUID userId, UUID resource_instance_id, OperationType operation, ResourceType resource) {
		return permissionRepository.hasPermission(userId, resource_instance_id, operation, resource);
	}

	@Override
	public void addResourceRolePermission(UUID userId, UUID resourceInstanceId, RoleType roleType, ResourceType resourceType) {
		permissionRepository.addResourceRolePermission(userId, resourceInstanceId, roleType, resourceType);
	}
	
}
