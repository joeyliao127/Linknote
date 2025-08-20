package com.penguin.linknote.domain.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteCommand {
    private String title;
    private String content;
    private String question;
    private String keypoint;
}
