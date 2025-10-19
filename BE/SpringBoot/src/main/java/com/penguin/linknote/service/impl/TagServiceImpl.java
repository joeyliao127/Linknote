package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.common.service.PaginationService;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final PaginationService paginationService;

    public TagServiceImpl(TagRepository tagRepository, PaginationService paginationService) {
        this.tagRepository = tagRepository;
        this.paginationService = paginationService;
    }

    @Override
    public PageResponse<TagDTO> indexTags(UUID userId, PageCommand pageCommand) {
        PageCommand normalPageCommand = paginationService.normalizePageCommand(pageCommand);
        Pageable pageable = PageRequest.of(normalPageCommand.getPage(), normalPageCommand.getPageSize());
        Page<Tag> tagList = tagRepository.findByUserId(userId, pageable);

        return  PageResponse.fromPage(tagList, TagDTO::fromEntity);
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
        Tag existingTag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag Not found. TagId:" + tagId));

        existingTag.setId(existingTag.getId());
        existingTag.setUserId(existingTag.getUserId());
        existingTag.setTitle(tagCommand.getTitle() == null ? existingTag.getTitle() : tagCommand.getTitle());
        existingTag.setCreatedAt(existingTag.getCreatedAt());
        existingTag.setUpdatedAt(Instant.now());
        return TagDTO.fromEntity(tagRepository.save(existingTag));
    }

    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.deleteById(tagId);
    }
}
