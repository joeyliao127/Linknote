package com.penguin.linknote.domain.resources;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourcesUpdateCommand {
    @NotBlank
    private String title;
}
