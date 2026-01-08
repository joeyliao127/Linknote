package com.penguin.linknote.repository;

import com.penguin.linknote.entity.!{upper};
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.!{lower}.!{upper}Condition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface !{upper}Repository {
    List<!{upper}> index(Integer limit);

    PageResponse<!{upper}> paginate(int page, int limit, !{upper}Condition condition);

    Optional<!{upper}> get(UUID id);

    !{upper} create(!{upper} entity);

    !{upper} update(!{upper} entity);

    void delete(UUID id);
}
