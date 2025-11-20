package com.penguin.linknote.domain.rbac;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.penguin.linknote.service.PermissionService;

@Component
public class RBACAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>{

	private final PermissionService permissionService;

	public RBACAuthorizationManager(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {

		Authentication auth = authentication.get();

		if(auth == null || !auth.isAuthenticated()) {
			return new AuthorizationDecision(false);
		}

		UUID userId = (UUID) auth.getPrincipal();
		String path = context.getRequest().getRequestURI();
		String method = context.getRequest().getMethod();

		ParsedResource parsed = PathResolver.resolve(path);

		// 無需 ACL → 自動 allow
		if (parsed == null) {
			return new AuthorizationDecision(true);
		}

		ResourceType resourceType = parsed.resourceType();
		OperationType operationType = OperationType.fromHttpMethod(method);

		boolean allowed = permissionService.hasPermission(
				userId,
				parsed.targetId(),
				operationType,
				resourceType
		);

		return new AuthorizationDecision(allowed);
	}
	
}
