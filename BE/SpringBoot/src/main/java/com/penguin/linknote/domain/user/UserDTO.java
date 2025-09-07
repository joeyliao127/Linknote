package com.penguin.linknote.domain.user;

import com.penguin.linknote.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .enabled(user.getUserStatusId() == 1)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
