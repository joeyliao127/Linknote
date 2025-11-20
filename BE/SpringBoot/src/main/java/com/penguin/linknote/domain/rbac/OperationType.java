package com.penguin.linknote.domain.rbac;

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
		return OperationType.from((method));
	}
}

