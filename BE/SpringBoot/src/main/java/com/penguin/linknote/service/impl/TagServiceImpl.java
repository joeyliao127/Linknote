package com.penguin.linknote.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagCondition;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.domain.tag.exception.TagNotFoundException;
import com.penguin.linknote.entity.Tag;
import com.penguin.linknote.repository.TagRepository;
import com.penguin.linknote.service.TagService;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public PageResponse<TagDTO> index(TagCondition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        PageResponse<Tag> tagPage = tagRepository.paginate(page, limit, condition);

        PageResponse<TagDTO> response = new PageResponse<>();
        response.setCount(tagPage.getCount());
        response.setCurrentPage(tagPage.getCurrentPage());
        response.setPageSize(tagPage.getPageSize());
        response.setTotalPage(tagPage.getTotalPage());
        response.setItems(tagPage.getItems().stream().map(TagDTO::fromEntity).toList());
        return response;
    }

    @Override
    public TagDTO create(UUID userId, TagCommand tagCommand) {
        Tag tag = new Tag();

        tag.setId(UUID.randomUUID());
        tag.setUserId(userId);
        tag.setTitle(tagCommand.getTitle());
        tag.setCreatedAt(Instant.now());
        tag.setUpdatedAt(Instant.now());

        return TagDTO.fromEntity(tagRepository.create(tag));
    }

    @Override
    public TagDTO update(UUID tagId, TagCommand tagCommand) {
        Tag existingTag = tagRepository.get(tagId)
                .orElseThrow(() -> new TagNotFoundException("Tag Not found. TagId:" + tagId));

        existingTag.setId(existingTag.getId());
        existingTag.setUserId(existingTag.getUserId());
        existingTag.setTitle(tagCommand.getTitle() == null ? existingTag.getTitle() : tagCommand.getTitle());
        existingTag.setCreatedAt(existingTag.getCreatedAt());
        existingTag.setUpdatedAt(Instant.now());
        return TagDTO.fromEntity(tagRepository.update(existingTag));
    }

    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.delete(tagId);
    }
}
