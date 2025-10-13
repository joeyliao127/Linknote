package com.penguin.linknote.controller.tag;

import com.penguin.linknote.common.command.PageCommand;
import com.penguin.linknote.common.dto.ApiResponse;
import com.penguin.linknote.common.dto.PageResponse;
import com.penguin.linknote.domain.tag.TagCommand;
import com.penguin.linknote.domain.tag.TagDTO;
import com.penguin.linknote.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public ResponseEntity<PageResponse<TagDTO>> index(PageCommand pageCommand, @RequestHeader(name = "Authorization") UUID userId) {
        PageResponse<TagDTO> tagDTOList = tagService.indexTags(userId, pageCommand);
        return ResponseEntity.ok(tagDTOList);
    }

    @PostMapping
    public ResponseEntity<TagDTO> post(@RequestBody TagCommand tagCommand, @RequestHeader(name = "Authorization") UUID userId) {
        TagDTO tagDTO = tagService.createTag(userId, tagCommand);
        return ResponseEntity.ok(tagDTO);
    }
    @PutMapping("/{tagId}")
    public ResponseEntity<TagDTO> update(@PathVariable String tagId,  @RequestBody TagCommand tagCommand) {
        TagDTO tagDTO = tagService.updateTag(UUID.fromString(tagId), tagCommand);
        return ResponseEntity.ok(tagDTO);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> delete(@RequestParam String tagId) {
        tagService.deleteTag(UUID.fromString(tagId));
        return ResponseEntity.ok(new ApiResponse(true, "Delete tag successfully!"));
    }

}
