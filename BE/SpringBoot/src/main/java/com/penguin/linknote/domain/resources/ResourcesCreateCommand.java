package com.penguin.linknote.domain.resources;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourcesCreateCommand {

    @NotBlank
    private String title;
}
