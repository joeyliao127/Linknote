package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.NoteFilter;
import com.penguin.linknote.domain.note.exception.NoteNotFoundException;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.entity.*;
import com.penguin.linknote.repository.NoteRepository;
import com.penguin.linknote.repository.NoteTagsRepository;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.service.NoteService;
import com.querydsl.core.BooleanBuilder;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final NoteTagsRepository noteTagsRepository;
    private final PaginationService paginationService;
    private final JPAQueryFactory jpaQueryFactory;

    public NoteServiceImpl(
            NoteRepository noteRepository,
            TagRepository tagRepository,
            NoteTagsRepository noteTagsRepository,
            PaginationService paginationService,
            JPAQueryFactory jpaQueryFactory) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
        this.noteTagsRepository = noteTagsRepository;
        this.paginationService = paginationService;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public PageResponse<NoteDTO> indexNotes(NoteFilter filter, PageCommand pageCommand) {
        QNote qNote = QNote.note;
        QTag qTag = QTag.tag;
        QNoteTag qNoteTag = QNoteTag.noteTag;

        JPAQuery<Note> query = jpaQueryFactory.selectDistinct(qNote).from(qNote);
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNote.notebookId.eq(filter.getNotebookId()));

        if (filter.getTitle() != null && !filter.getTitle().isEmpty())
            builder.and(qNote.title.startsWith(filter.getTitle()));
        if (filter.getStar() != null) builder.and(QNote.note.star.eq(filter.getStar()));

        if (filter.getTagIdList() != null && !filter.getTagIdList().isEmpty()) {
            query.join(qNoteTag).on(qNoteTag.note.id.eq(qNote.id))
                    .where(builder.and(qNoteTag.tag.id.in(filter.getTagIdList())));
        } else {
            query.where(builder);
        }

        // 1. 取得 note
        // 2. 解析出 noteIds
        // 3. JOIN note_tags，取得 Map<noteId, List<Tag>>
        // 4. stream set note's tags.

        PageResponse<NoteDTO> notes = paginationService.applyPagination(query, pageCommand, NoteDTO::fromEntity);

        if (notes.getItems().isEmpty()) return notes;

        // Get note ids.
        UUID[] noteIds = notes.getItems().stream().map(NoteDTO::getId).toArray(UUID[]::new);

        // Get note tags and groupBy noteId.
        List<Tuple> results = jpaQueryFactory
                .select(qNote.id, qTag)
                .from(qNoteTag)
                .join(qNote).on(qNoteTag.note.id.eq(qNote.id))
                .join(qTag).on(qNoteTag.tag.id.eq(qTag.id))
                .where(qNoteTag.note.id.in(noteIds))
                .orderBy(qNote.title.asc())
                .fetch();

        Map<UUID, List<TagDTO>> noteTags = results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(qNote.id),
                        Collectors.mapping(
                                tuple -> TagDTO.fromEntity(Objects.requireNonNull(tuple.get(qTag))),
                                Collectors.toList()
                        )
                ));

        notes.getItems().forEach(note -> {
            note.setTags(noteTags.getOrDefault(note.getId(), Collections.emptyList()));
        });

        return notes;
    }

    @Override
    public NoteDTO getNoteById(UUID noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note note found."));
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
        Note exsitNote = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note note found."));

        exsitNote.setId(noteId);
        exsitNote.setTitle(noteCommand.getTitle() == null ? exsitNote.getTitle() : noteCommand.getTitle());
        exsitNote.setContent(noteCommand.getContent() == null ? exsitNote.getContent() : noteCommand.getContent());
        exsitNote.setKeypoint(noteCommand.getKeypoint() == null ? exsitNote.getKeypoint() : noteCommand.getKeypoint());
        exsitNote.setQuestion(noteCommand.getQuestion() == null ? exsitNote.getQuestion() : noteCommand.getQuestion());
        exsitNote.setStar(noteCommand.getStar() == null ? exsitNote.getStar() : noteCommand.getStar());
        exsitNote.setUpdatedAt(Instant.now());
        return NoteDTO.fromEntity(noteRepository.save(exsitNote));
    }

    @Override
    public void deleteNote(UUID noteId) {
        noteRepository.deleteById(noteId);
    }

    @Override
    @Transactional
    public void addTagToNote(UUID noteId, List<UUID> tagIdList) {
        // 1. 查詢 Note（如果不存在會拋出異常）
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found."));
        
        // 2. 刪除現有的所有 tags（批次刪除）
        noteTagsRepository.deleteByNoteId(noteId);
        
        // 3. 批次查詢所有要新增的 tags
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        
        // 驗證：確保所有 tagId 都存在
        if (tagList.size() != tagIdList.size()) {
            throw new IllegalArgumentException("Some tag IDs do not exist");
        }
        
        // 4. 建立新的 NoteTag 關聯
        List<NoteTag> noteTagList = tagList.stream()
                .map(tag -> {
                    NoteTag noteTag = new NoteTag();
                    
                    // ✅ 關鍵：先設定複合主鍵
                    NoteTagId noteTagId = new NoteTagId(noteId, tag.getId());
                    noteTag.setId(noteTagId);
                    
                    // 再設定關聯
                    noteTag.setNote(note);
                    noteTag.setTag(tag);
                    
                    // 時間戳會由 @PrePersist 自動設定，但也可以手動設定
                    noteTag.setCreatedAt(Instant.now());
                    noteTag.setUpdatedAt(Instant.now());
                    
                    return noteTag;
                })
                .toList();
        
        // 5. 批次儲存
        noteTagsRepository.saveAll(noteTagList);
        
        // 6. 更新 Note 的時間戳
        note.setUpdatedAt(Instant.now());
        noteRepository.save(note);
    }

}
