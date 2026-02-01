package com.penguin.linknote.service;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.!{lower}.!{upper}CreateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}UpdateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}DTO;
import com.penguin.linknote.domain.!{lower}.!{upper}Condition;

import java.util.UUID;

public interface !{upper}Service {
    // TODO: 支援 star, tag, order by desc 等 filters
    PageResponse<!{upper}DTO> index(!{upper}Condition condition, PageCommand pageCommand);

    !{upper}DTO create(!{upper}CreateCommand !{lower}CreateCommand);

    !{upper}DTO update(UUID !{lower}Id, !{upper}UpdateCommand !{lower}UpdateCommand);

    void delete(UUID !{lower}Id);

}
