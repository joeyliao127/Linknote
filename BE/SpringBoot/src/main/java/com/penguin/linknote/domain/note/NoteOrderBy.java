package com.penguin.linknote.domain.note;

public enum NoteOrderBy {
    TITLE("title", "title"),
    CREATED_AT("created_at", "createdat", "created_at"),
    UPDATED_AT("updated_at", "updatedat", "updated_at", "update_at"),
    STAR("star", "star");

    private final String column;
    private final String[] aliases;

    NoteOrderBy(String column, String... aliases) {
        this.column = column;
        this.aliases = aliases;
    }

    public String getColumn() {
        return column;
    }

    public static NoteOrderBy from(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        for (NoteOrderBy orderBy : values()) {
            for (String alias : orderBy.aliases) {
                if (alias.equals(normalized)) {
                    return orderBy;
                }
            }
        }
        return null;
    }
}
