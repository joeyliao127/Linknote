package com.penguin.linknote.domain.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserCommand {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String email;
}
