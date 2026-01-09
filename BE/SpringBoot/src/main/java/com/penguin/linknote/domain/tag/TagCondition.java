package com.penguin.linknote.domain.tag;

import java.util.UUID;

import lombok.Data;

@Data
public class TagCondition {
    private UUID userId;
    private String title;
    private String orderBy;
    private String orderDirection;
}
