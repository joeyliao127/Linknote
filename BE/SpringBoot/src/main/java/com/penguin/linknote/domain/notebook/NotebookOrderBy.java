package com.penguin.linknote.domain.notebook;

public enum NotebookOrderBy {
    CREATED_AT("n.created_at", "createdat", "created_at"),
    UPDATED_AT("n.updated_at", "updatedat", "updated_at", "update_at"),
    ACTIVE("n.is_active", "active", "is_active");

    private final String column;
    private final String[] aliases;

    NotebookOrderBy(String column, String... aliases) {
        this.column = column;
        this.aliases = aliases;
    }

    public String getColumn() {
        return column;
    }

    public static NotebookOrderBy from(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        for (NotebookOrderBy orderBy : values()) {
            for (String alias : orderBy.aliases) {
                if (alias.equals(normalized)) {
                    return orderBy;
                }
            }
        }
        return null;
    }
}
