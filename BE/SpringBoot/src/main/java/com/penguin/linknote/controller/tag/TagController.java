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
    private final UUID userId;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
        userId = UUID.fromString("a60b42ae-0659-4f89-aa31-0442b56642eb");
    }


    @GetMapping
    public ResponseEntity<PageResponse<TagDTO>> index(@RequestParam UUID noteId, PageCommand pageCommand) {
        PageResponse<TagDTO> tagDTOList = tagService.indexTags(userId, noteId, pageCommand);
        return ResponseEntity.ok(tagDTOList);
    }

    @PostMapping
    public ResponseEntity<TagDTO> post(@RequestBody TagCommand tagCommand) {
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
