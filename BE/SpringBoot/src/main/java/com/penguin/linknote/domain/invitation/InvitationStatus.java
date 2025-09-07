package com.penguin.linknote.domain.invitation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum InvitationStatus {
    PENDING("pending"),
    ACCEPT("accept"),
    REJECT("reject");

    private final String title;

    public static InvitationStatus fromTitle(String title) {
        return Arrays.stream(values())
                .filter(status -> status.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + title));
    }
}
