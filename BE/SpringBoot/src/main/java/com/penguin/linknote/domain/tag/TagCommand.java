package com.penguin.linknote.domain.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagCommand {
    @NotBlank
    private String title;
}
