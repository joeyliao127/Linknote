package com.penguin.linknote.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCondition;
import com.penguin.linknote.entity.Tag;

public interface TagRepository {
    List<Tag> index(UUID userId, Integer limit);

    PageResponse<Tag> paginate(int page, int limit, TagCondition condition);

    Optional<Tag> get(UUID id);

    Tag create(Tag tag);

    Tag update(Tag tag);

    void delete(UUID id);

    List<Tag> findAllById(List<UUID> ids);
}
