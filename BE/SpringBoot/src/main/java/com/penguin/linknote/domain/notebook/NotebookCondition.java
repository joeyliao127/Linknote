package com.penguin.linknote.domain.notebook;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotebookCondition {
    private UUID userId;
    private String title;
    private Boolean active;
    private Boolean collab;
    private String orderBy;
    private String orderDirection;
}
