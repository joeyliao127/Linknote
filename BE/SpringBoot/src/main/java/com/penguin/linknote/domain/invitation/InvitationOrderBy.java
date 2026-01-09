package com.penguin.linknote.domain.invitation;

public enum InvitationOrderBy {
    CREATED_AT("created_at", "createdat", "created_at"),
    UPDATED_AT("updated_at", "updatedat", "updated_at", "update_at");

    private final String column;
    private final String[] aliases;

    InvitationOrderBy(String column, String... aliases) {
        this.column = column;
        this.aliases = aliases;
    }

    public String getColumn() {
        return column;
    }

    public static InvitationOrderBy from(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        for (InvitationOrderBy orderBy : values()) {
            for (String alias : orderBy.aliases) {
                if (alias.equals(normalized)) {
                    return orderBy;
                }
            }
        }
        return null;
    }
}
