package com.penguin.linknote.domain.notebook;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotebookCommand {

    @NotEmpty
    private String title;
    private String description;
    private Boolean active;

    public NotebookCommand(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
