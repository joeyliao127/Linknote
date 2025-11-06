package com.penguin.linknote.domain.roles;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolesUpdateCommand {

    @NotBlank
    private String title;
    
}
