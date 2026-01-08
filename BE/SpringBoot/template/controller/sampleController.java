package com.penguin.linknote.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.!{lower}.!{upper}Condition;
import com.penguin.linknote.domain.!{lower}.!{upper}CreateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}UpdateCommand;
import com.penguin.linknote.domain.!{lower}.!{upper}DTO;
import com.penguin.linknote.domain.!{lower}.!{upper}OrderBy;
import com.penguin.linknote.domain.!{lower}.exception.Invalid!{upper}ParameterException;
import com.penguin.linknote.service.!{upper}Service;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/!{lower}s")
@Validated
public class !{upper}Controller {

    //TODO: other services if needed
    private final !{upper}Service !{lower}Service;
    private final String path;

    //TODO: auto awire other services if needed
    public !{upper}Controller(!{upper}Service !{lower}Service) {
        this.!{lower}Service = !{lower}Service;
        this.path = "/!{lower}s";
    }

    @GetMapping
    public ResponseEntity<PageResponse<!{upper}DTO>> index(
            //TODO: customize filter params
            // @RequestParam(required = false) String title,
            // @RequestParam(required = false) Boolean active,
            // @RequestHeader(name = "Authorization") UUID userId,
            @ModelAttribute !{upper}Condition !{lower}Condition,
            PageCommand pageCommand)
    {
        if (!{lower}Condition != null && !{lower}Condition.getOrderBy() != null
                && !{lower}Condition.getOrderBy().isBlank()) {
            if (!{upper}OrderBy.from(!{lower}Condition.getOrderBy()) == null) {
                throw new Invalid!{upper}ParameterException();
            }
        }
        PageResponse<!{upper}DTO> !{lower}DTOList = !{lower}Service.index(!{lower}Condition, pageCommand);
        return ResponseEntity.ok(!{lower}DTOList);
    }

    @PostMapping
    public ResponseEntity<!{upper}DTO> create(@RequestBody @Valid !{upper}CreateCommand !{lower}CreateCommand) {
        !{upper}DTO !{lower} = !{lower}Service.create(!{lower}CreateCommand);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/api/" + this.path + "/" + !{lower}.getId()))
                .body(!{lower});
    }

    @PutMapping("/{!{lower}Id}")
    public ResponseEntity<!{upper}DTO> update(@PathVariable UUID !{lower}Id, @RequestBody @Valid !{upper}UpdateCommand !{lower}UpdateCommand) {
        !{upper}DTO !{lower}DTO = !{lower}Service.update(!{lower}Id, !{lower}UpdateCommand);

        return ResponseEntity.ok(!{lower}DTO);
    }

    @DeleteMapping("/{!{lower}Id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID !{lower}Id) {
        !{lower}Service.delete(!{lower}Id);
        return ResponseEntity.ok(new ApiResponse(true, "Delete !{lower} successfully!"));
    }
}
