package com.penguin.linknote.domain.notebook;

import com.penguin.linknote.entity.Notebook;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class NotebookDTO {
    private UUID id;
    private String title;
    private String description;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static NotebookDTO fromEntity(Notebook notebook) {
        return NotebookDTO.builder()
                .id(notebook.getId())
                .title(notebook.getTitle())
                .description(notebook.getDescription())
                .active(notebook.getIsActive())
                .createdAt(notebook.getCreatedAt())
                .updatedAt(notebook.getUpdatedAt())
                .build();
    }

    public static List<NotebookDTO> fromEntityList(List<Notebook> notebooks) {
        return notebooks.stream().map(NotebookDTO::fromEntity).toList();
    }
}
