package com.penguin.linknote.domain.rbac;

import java.util.Map;

public enum OperationType {
	CREATE,
	DELETE,
	READ,
	UPDATE;

	public static OperationType from(String value) {
		try {
			return OperationType.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown operation type: " + value);
		}
	}

	public static OperationType fromHttpMethod(String method) {
		Map<String, String> methodToOperation = Map.of(
			"POST", "CREATE",
			"GET", "READ",
			"PUT", "UPDATE",
			"DELETE", "DELETE"
		);
		return OperationType.from(methodToOperation.get(method.toUpperCase()));
	}
}

