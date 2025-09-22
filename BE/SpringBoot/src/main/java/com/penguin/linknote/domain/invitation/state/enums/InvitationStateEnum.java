package com.penguin.linknote.domain.invitation.state.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum InvitationStateEnum {
    PENDING("pending"),
    ACCEPT("accepted"),
    REJECT("rejected"),
    RESEND("resend");

    private final String title;

    @JsonValue
    public String getTitle() {
        return title;
    }

    @JsonCreator
    public static InvitationStateEnum fromTitle(String title) {
        return Arrays.stream(values())
                .filter(status -> status.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid action: " + title));
    }
}
