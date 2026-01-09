package com.penguin.linknote.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class User {

    private UUID id;

    private String email;

    private String username;

    private String password;

    private int userStatusId;

    private Instant createdAt;

    private Instant updatedAt;

    public User() {}

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

}
