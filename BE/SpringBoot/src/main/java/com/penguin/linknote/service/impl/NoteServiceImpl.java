package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.note.NoteCommand;
import com.penguin.linknote.domain.note.NoteCondition;
import com.penguin.linknote.domain.note.NoteDTO;
import com.penguin.linknote.domain.note.exception.NoteNotFoundException;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.entity.Note;
import com.penguin.linknote.entity.NoteTag;
import com.penguin.linknote.entity.NoteTagId;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.NoteRepository;
import com.penguin.linknote.repository.NoteTagsRepository;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.service.NoteService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final NoteTagsRepository noteTagsRepository;

    public NoteServiceImpl(
            NoteRepository noteRepository,
            TagRepository tagRepository,
            NoteTagsRepository noteTagsRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
        this.noteTagsRepository = noteTagsRepository;
    }

    @Override
    public PageResponse<NoteDTO> indexNotes(NoteCondition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        PageResponse<Note> notePage = noteRepository.paginate(page, limit, condition);

        PageResponse<NoteDTO> response = new PageResponse<>();
        response.setCount(notePage.getCount());
        response.setCurrentPage(notePage.getCurrentPage());
        response.setPageSize(notePage.getPageSize());
        response.setTotalPage(notePage.getTotalPage());
        List<NoteDTO> noteDTOs = notePage.getItems().stream().map(NoteDTO::fromEntity).toList();
        response.setItems(noteDTOs);

        if (noteDTOs.isEmpty()) {
            return response;
        }

        List<UUID> noteIds = noteDTOs.stream().map(NoteDTO::getId).toList();
        Map<UUID, List<Tag>> noteTags = noteTagsRepository.findTagsByNoteIds(noteIds);
        noteDTOs.forEach(note -> {
            List<TagDTO> tags = noteTags.getOrDefault(note.getId(), Collections.emptyList())
                    .stream()
                    .map(TagDTO::fromEntity)
                    .toList();
            note.setTags(tags);
        });

        return response;
    }

    @Override
    public NoteDTO get(UUID noteId) {
        Note note = noteRepository.get(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note note found."));

        List<TagDTO> tags = noteTagsRepository.findTagsByNoteId(noteId)
                .stream()
                .map(TagDTO::fromEntity)
                .toList();

        NoteDTO noteDTO = NoteDTO.fromEntity(note);
        noteDTO.setTags(tags);
        return noteDTO;
    }

    @Override
    public NoteDTO create(NoteCommand noteCommand) {
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
        return NoteDTO.fromEntity(noteRepository.create(note));
    }

    @Override
    public NoteDTO update(UUID noteId, NoteCommand noteCommand) {
        Note exsitNote = noteRepository.get(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note note found."));

        exsitNote.setId(noteId);
        exsitNote.setTitle(noteCommand.getTitle() == null ? exsitNote.getTitle() : noteCommand.getTitle());
        exsitNote.setContent(noteCommand.getContent() == null ? exsitNote.getContent() : noteCommand.getContent());
        exsitNote.setKeypoint(noteCommand.getKeypoint() == null ? exsitNote.getKeypoint() : noteCommand.getKeypoint());
        exsitNote.setQuestion(noteCommand.getQuestion() == null ? exsitNote.getQuestion() : noteCommand.getQuestion());
        exsitNote.setStar(noteCommand.getStar() == null ? exsitNote.getStar() : noteCommand.getStar());
        exsitNote.setUpdatedAt(Instant.now());
        return NoteDTO.fromEntity(noteRepository.update(exsitNote));
    }

    @Override
    public void delete(UUID noteId) {
        noteRepository.delete(noteId);
    }

    /**
     * addTag 是將 note's tag 一次更新為 tagIdList 中的 ids，而非 toggle 加入
     */
    @Override
    @Transactional
    public void addTagToNote(UUID noteId, List<UUID> tagIdList) {
        // 1. 查詢 Note（如果不存在會拋出異常）
        Note note = noteRepository.get(noteId)
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
                    
                    // 時間戳需手動設定
                    noteTag.setCreatedAt(Instant.now());
                    noteTag.setUpdatedAt(Instant.now());
                    
                    return noteTag;
                })
                .toList();
        
        // 5. 批次儲存
        noteTagsRepository.saveAll(noteTagList);
        
        // 6. 更新 Note 的時間戳
        note.setUpdatedAt(Instant.now());
        noteRepository.update(note);
    }

}
