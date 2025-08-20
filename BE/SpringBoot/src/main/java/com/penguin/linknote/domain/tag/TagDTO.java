package com.penguin.linknote.domain.tag;

import com.penguin.linknote.entity.Note;
import com.penguin.linknote.entity.Tag;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class TagDTO {
    private UUID id;
    private String title;
    private Instant createdAt;

    public static TagDTO fromEntity(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .title(tag.getTitle())
                .createdAt(tag.getCreatedAt())
                .build();
    }

    public static List<TagDTO> fromEntity(List<Tag>  tagList) {
        return tagList.stream().map(TagDTO::fromEntity).toList();
    }
}
