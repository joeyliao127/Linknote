package com.penguin.linknote.domain.tag;

import com.penguin.linknote.entity.Tag;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TagDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static TagDTO fromEntity(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .userId(tag.getUserId())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }

    public static List<TagDTO> fromEntityList(List<Tag>  tagList) {
        return tagList.stream().map(TagDTO::fromEntity).toList();
    }
}
