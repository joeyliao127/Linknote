package com.penguin.linknote.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String title;

    public static UserStatus fromTitle(String title) {
        return Arrays.stream(values())
                .filter(status -> status.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + title));
    }

}
