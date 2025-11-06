package com.penguin.linknote.domain.operations;

import java.time.Instant;
import java.util.List;

import com.penguin.linknote.entity.Operations;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OperationsDTO {

    private int id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static OperationsDTO fromEntity(Operations operations) {
        return OperationsDTO.builder()
            .id(operations.getId())
            .title(operations.getTitle())
            .createdAt(operations.getCreatedAt())
            .updatedAt(operations.getUpdatedAt())
            .build();
    }

    public static List<OperationsDTO> fromEntityList(List<Operations> operationsList) {
        return operationsList.stream().map(OperationsDTO::fromEntity).toList();
    }
}
