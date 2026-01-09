package com.penguin.linknote.entity;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Tag {

    private UUID id;

    private String title;

    private UUID userId;

    private Instant createdAt;

    private Instant updatedAt;

}
