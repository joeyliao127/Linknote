package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagCondition;
import com.penguin.linknote.domain.tag.TagDTO;
import java.util.UUID;

public interface TagService {

    PageResponse<TagDTO> index(TagCondition condition, PageCommand pageCommand);
    TagDTO create(UUID userId, TagCommand tagCommand);
    TagDTO update(UUID tagId, TagCommand tagCommand);
    void deleteTag(UUID tagId);
}
