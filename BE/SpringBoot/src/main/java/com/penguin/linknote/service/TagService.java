package com.penguin.linknote.service;

import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;

import java.util.List;
import java.util.UUID;

public interface TagService {

    List<TagDTO> indexTags(UUID userId, UUID noteId);
    TagDTO createTag(UUID userId, TagCommand tagCommand);
    TagDTO updateTag(UUID tagId, TagCommand tagCommand);
    void deleteTag(UUID tagId);
}
