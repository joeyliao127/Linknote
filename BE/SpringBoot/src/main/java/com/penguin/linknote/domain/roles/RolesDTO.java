package com.penguin.linknote.domain.roles;

import java.time.Instant;
import java.util.List;

import com.penguin.linknote.entity.Roles;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RolesDTO {

    private int id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static RolesDTO fromEntity(Roles roles) {
        return RolesDTO.builder()
            .id(roles.getId())
            .title(roles.getTitle())
            .createdAt(roles.getCreatedAt())
            .updatedAt(roles.getUpdatedAt())
            .build();
    }

    public static List<RolesDTO> fromEntityList(List<Roles> rolesList) {
        return rolesList.stream().map(RolesDTO::fromEntity).toList();
    }
}
