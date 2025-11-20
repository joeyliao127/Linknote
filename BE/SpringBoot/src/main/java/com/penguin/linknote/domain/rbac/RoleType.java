package com.penguin.linknote.domain.rbac;

public enum RoleType {
	ROLE_OWNER,
	ROLE_COLLABORATOR,
	ROLE_MEMBER,
	ROLE_GUEST;

	public static RoleType from(String value) {
		try {
			return RoleType.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown role type: " + value);
		}
	}
}
