package com.penguin.linknote.service;

import java.util.UUID;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;

public interface TagService {

    PageResponse<TagDTO> index(UUID userId, PageCommand pageCommand);
    TagDTO create(UUID userId, TagCommand tagCommand);
    TagDTO update(UUID tagId, TagCommand tagCommand);
    void deleteTag(UUID tagId);
}
