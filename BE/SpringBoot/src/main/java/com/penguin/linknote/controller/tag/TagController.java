package com.penguin.linknote.controller.tag;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagCondition;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.domain.tag.TagOrderBy;
import com.penguin.linknote.domain.tag.exception.InvalidTagParameterException;
import com.penguin.linknote.service.TagService;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public ResponseEntity<PageResponse<TagDTO>> index(
            @ModelAttribute TagCondition condition,
            PageCommand pageCommand,
            Authentication authentication) {
        if (condition == null) {
            condition = new TagCondition();
        }
        if (condition.getOrderBy() != null && !condition.getOrderBy().isBlank()) {
            if (TagOrderBy.from(condition.getOrderBy()) == null) {
                throw new InvalidTagParameterException();
            }
        }
        UUID userId = (UUID) authentication.getPrincipal();
        condition.setUserId(userId);
        PageResponse<TagDTO> tagDTOList = tagService.index(condition, pageCommand);
        return ResponseEntity.ok(tagDTOList);
    }

    @PostMapping
    public ResponseEntity<TagDTO> create(@RequestBody TagCommand tagCommand, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        TagDTO tagDTO = tagService.create(userId, tagCommand);
        return ResponseEntity.ok(tagDTO);
    }
    @PutMapping("/{tagId}")
    public ResponseEntity<TagDTO> update(@PathVariable String tagId,  @RequestBody TagCommand tagCommand) {
        TagDTO tagDTO = tagService.update(UUID.fromString(tagId), tagCommand);
        return ResponseEntity.ok(tagDTO);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String tagId) {
        tagService.deleteTag(UUID.fromString(tagId));
        return ResponseEntity.ok(new ApiResponse(true, "Delete tag successfully!"));
    }

}
