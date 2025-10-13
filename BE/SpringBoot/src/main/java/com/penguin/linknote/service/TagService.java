package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;

import java.util.UUID;

public interface TagService {

    PageResponse<TagDTO> indexTags(UUID userId, PageCommand pageCommand);
    TagDTO createTag(UUID userId, TagCommand tagCommand);
    TagDTO updateTag(UUID tagId, TagCommand tagCommand);
    void deleteTag(UUID tagId);
}
