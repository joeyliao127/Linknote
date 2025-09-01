package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.exception.note.NoteNotFoundException;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.entity.Note;
import com.penguin.linknote.repository.NoteRepository;
import com.penguin.linknote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl  implements NoteService {

    private final NoteRepository noteRepository;
    private final PaginationService paginationService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PaginationService paginationService) {
        this.noteRepository = noteRepository;
        this.paginationService = paginationService;
    }

    @Override
    public PageResponse<NoteDTO> indexNotesByNotebookId(UUID notebookId, PageCommand pageCommand) {
        PageCommand normalizeCommand = paginationService.normalizePageCommand(pageCommand);
        Pageable pageable = PageRequest.of(normalizeCommand.getPage(), normalizeCommand.getPageSize(), Sort.by("createdAt").descending());
        Page<Note> notePage = noteRepository.findByNotebookId(notebookId, pageable);
        return PageResponse.fromPage(notePage, NoteDTO::fromEntity);
    }

    @Override
    public NoteDTO getNoteById(UUID noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(()-> new NoteNotFoundException("Note note found."));
        return NoteDTO.fromEntity(note);
    }

    @Override
    public NoteDTO createNote(NoteCommand noteCommand) {
        Note note = new Note();
        note.setId(UUID.randomUUID());
        note.setNotebookId(UUID.fromString(noteCommand.getNotebookId()));
        note.setTitle(noteCommand.getTitle());
        note.setContent(noteCommand.getContent());
        note.setKeypoint(noteCommand.getKeypoint());
        note.setQuestion(noteCommand.getQuestion());
        note.setStar(false);
        note.setCreatedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return NoteDTO.fromEntity(noteRepository.save(note));
    }

    @Override
    public NoteDTO updateNote(UUID noteId, NoteCommand noteCommand) {
        Note exsitNote = noteRepository.findById(noteId).orElseThrow(()-> new NoteNotFoundException("Note note found."));
        Note note = new Note();
        note.setId(noteId);
        note.setNotebookId(UUID.fromString(noteCommand.getNotebookId()));
        note.setTitle(noteCommand.getTitle() == null ? exsitNote.getTitle() : noteCommand.getTitle());
        note.setContent(noteCommand.getContent()  == null ? exsitNote.getContent() : noteCommand.getContent());
        note.setKeypoint(noteCommand.getKeypoint() == null ? exsitNote.getKeypoint() : noteCommand.getKeypoint());
        note.setQuestion(noteCommand.getQuestion()  == null ? exsitNote.getQuestion() : noteCommand.getQuestion());
        note.setStar(noteCommand.getStar() == null ? exsitNote.getStar() : noteCommand.getStar());
        note.setUpdatedAt(Instant.now());
        return NoteDTO.fromEntity(noteRepository.save(note));
    }

    @Override
    public void deleteNote(UUID noteId) {
        noteRepository.deleteById(noteId);
    }


}
