package com.penguin.linknote.domain.note;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteCommand {

    @NotEmpty
    private String notebookId;

    private String title;
    private String content;
    private String question;
    private String keypoint;
    private Boolean star;
}
