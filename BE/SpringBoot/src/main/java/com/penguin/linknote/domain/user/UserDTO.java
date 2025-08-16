package com.penguin.linknote.domain.user;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
