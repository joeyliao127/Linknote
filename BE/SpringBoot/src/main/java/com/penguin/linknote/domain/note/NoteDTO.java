package com.penguin.linknote.domain.note;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.entity.Note;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDTO {
    private UUID id;
    private UUID notebookId;
    private String title;
    private String content;
    private String question;
    private String keypoint;
    private Boolean star;
    private Instant createdAt;
    private Instant updatedAt;
    private List<TagDTO> tags;

    public static NoteDTO fromEntity(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .question(note.getQuestion())
                .content(note.getContent())
                .keypoint(note.getKeypoint())
                .star(note.getStar())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .notebookId(note.getNotebookId())
                .build();
    }

    public static List<NoteDTO> fromEntityList(List<Note> noteList) {
        return noteList.stream().map(NoteDTO::fromEntity).toList();
    }
}
