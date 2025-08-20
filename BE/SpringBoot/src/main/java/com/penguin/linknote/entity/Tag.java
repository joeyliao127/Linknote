package com.penguin.linknote.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Data
public class Tag {

    @Id
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
