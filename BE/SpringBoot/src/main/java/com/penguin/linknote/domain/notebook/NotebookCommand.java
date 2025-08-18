package com.penguin.linknote.domain.notebook;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class NotebookCommand {

    @NotEmpty
    private String title;
    private String description;
    private UUID userId;

    public NotebookCommand(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
