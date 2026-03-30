package com.penguin.linknote.domain.interaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InteractionAction {
    VIEW("VIEW"),
    LIKE("LIKE"),
    SHARE("SHARE"),
    BOOKMARK("BOOKMARK");

    private final String title;

    InteractionAction(String title) {
        this.title = title;
    }

    @JsonValue
    public String getTitle() {
        return title;
    }

    @JsonCreator
    public static InteractionAction fromTitle(String title) {
        if (title == null) return null;
        for (InteractionAction action : values()) {
            if (action.title.equalsIgnoreCase(title)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown interaction action: " + title);
    }
}
