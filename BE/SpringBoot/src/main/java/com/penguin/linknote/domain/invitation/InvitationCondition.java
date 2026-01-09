package com.penguin.linknote.domain.invitation;

import java.util.UUID;

import lombok.Data;

@Data
public class InvitationCondition {
    private UUID userId;
    private String orderBy;
    private String orderDirection;
}
