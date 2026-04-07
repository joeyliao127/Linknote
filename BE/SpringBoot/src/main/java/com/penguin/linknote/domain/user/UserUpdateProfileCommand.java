package com.penguin.linknote.domain.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserUpdateProfileCommand {
    @NotEmpty
    private String username;
}
