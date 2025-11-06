package com.penguin.linknote.domain.resources;

import java.time.Instant;
import java.util.List;

import com.penguin.linknote.entity.Resources;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourcesDTO {

    private int id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static ResourcesDTO fromEntity(Resources resources) {
        return ResourcesDTO.builder()
            .id(resources.getId())
            .title(resources.getTitle())
            .createdAt(resources.getCreatedAt())
            .updatedAt(resources.getUpdatedAt())
            .build();
    }

    public static List<ResourcesDTO> fromEntityList(List<Resources> resourcesList) {
        return resourcesList.stream().map(ResourcesDTO::fromEntity).toList();
    }
}
