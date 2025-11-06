package com.penguin.linknote.domain.roles;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RolesCreateCommand {
    @NotBlank
    private String title;
    
}
