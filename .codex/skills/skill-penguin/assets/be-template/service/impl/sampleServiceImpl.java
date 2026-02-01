package com.penguin.linknote.service.impl;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.!{lower}.!{upper}CreateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}UpdateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}Condition;
import com.penguin.linknote.domain.!{lower}.!{upper}DTO;
import com.penguin.linknote.domain.!{lower}.exception.!{upper}Exception;
import com.penguin.linknote.entity.!{upper};
import com.penguin.linknote.repository.!{upper}Repository;
import com.penguin.linknote.service.!{upper}Service;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class !{upper}ServiceImpl implements !{upper}Service {

    private final !{upper}Repository !{lower}Repository;

    public !{upper}ServiceImpl(
            !{upper}Repository !{lower}Repository)
    {
        this.!{lower}Repository = !{lower}Repository;
    }

    @Override
    public PageResponse<!{upper}DTO> index(!{upper}Condition condition, PageCommand pageCommand) {
        int page = pageCommand == null || pageCommand.getPage() == null ? 1 : pageCommand.getPage();
        int limit = pageCommand == null || pageCommand.getPageSize() == null ? 0 : pageCommand.getPageSize();

        PageResponse<!{upper}> result = !{lower}Repository.paginate(page, limit, condition);

        PageResponse<!{upper}DTO> response = new PageResponse<>();
        response.setCount(result.getCount());
        response.setCurrentPage(result.getCurrentPage());
        response.setPageSize(result.getPageSize());
        response.setTotalPage(result.getTotalPage());
        response.setItems(!{upper}DTO.fromEntityList(result.getItems()));
        return response;
    }
    

    @Override
    public !{upper}DTO create(!{upper}CreateCommand !{lower}Command) {
        !{upper} !{lower} = new !{upper}();
        !{lower}.setId(UUID.randomUUID());
        !{lower}.setCreatedAt(Instant.now());
        !{lower}.setUpdatedAt(Instant.now());

        return !{upper}DTO.fromEntity(!{lower}Repository.create(!{lower}));
    }

    @Override
    public !{upper}DTO update(UUID !{lower}Id, !{upper}UpdateCommand !{lower}Command) {
        !{upper} existing!{upper} = !{lower}Repository.get(!{lower}Id)
                .orElseThrow(() -> new !{upper}Exception("!{upper} not found"));
        existing!{upper}.setId(!{lower}Id);
        existing!{upper}.setUpdatedAt(Instant.now());

        return !{upper}DTO.fromEntity(!{lower}Repository.update(existing!{upper}));
    }

    @Override
    public void delete(UUID !{lower}Id) {
        !{lower}Repository.delete(!{lower}Id);
    }
}
