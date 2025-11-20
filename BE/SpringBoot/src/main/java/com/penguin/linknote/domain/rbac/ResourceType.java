package com.penguin.linknote.domain.rbac;

public enum ResourceType {
    NOTEBOOK,
    NOTE,
    TAG,
    COLLABORATOR,
    INVITATION;

    public static ResourceType from(String value) {
		try {
			return ResourceType.valueOf(value.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown resource type: " + value);
		}
	}

	public static ResourceType fromPath(String path) {
		String[] parts = path.split("/");

		//TODO: resource 定義暫時用解析第二個 index 的方式當作判斷，如 /api/:resource/...
		return ResourceType.from((parts[1]));
	}
}	