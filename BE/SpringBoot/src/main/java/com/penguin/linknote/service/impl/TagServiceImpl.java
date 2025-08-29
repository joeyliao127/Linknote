package com.penguin.linknote.service.impl;

import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagDTO> indexTags(UUID userId, UUID noteId) {
        //TODO: 做分頁 ＆ 查詢 note_tags 表，問 AI 如何使用
        List<Tag> tags = tagRepository.findByUserId(noteId);
        return TagDTO.fromEntityList(tags);
    }

    @Override
    public TagDTO createTag(UUID userId, TagCommand tagCommand) {
        Tag tag = new Tag();

        tag.setId(UUID.randomUUID());
        tag.setUserId(userId);
        tag.setTitle(tagCommand.getTitle());
        tag.setCreatedAt(Instant.now());
        tag.setUpdatedAt(Instant.now());

        return TagDTO.fromEntity(tagRepository.save(tag));
    }

    @Override
    public TagDTO updateTag(UUID tagId, TagCommand tagCommand) {
        Tag existingTag = tagRepository.findById(tagId).orElseThrow(()-> new EntityNotFoundException("Tag Not found. TagId:" + tagId));

        Tag tag = new Tag();
        tag.setId(existingTag.getId());
        tag.setUserId(existingTag.getUserId());
        tag.setTitle(tagCommand.getTitle() == null ? existingTag.getTitle() : tagCommand.getTitle());
        tag.setCreatedAt(existingTag.getCreatedAt());
        tag.setUpdatedAt(Instant.now());
        return TagDTO.fromEntity(tagRepository.save(tag));
    }

    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.deleteById(tagId);
    }
}
