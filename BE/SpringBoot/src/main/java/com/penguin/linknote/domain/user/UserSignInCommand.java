package com.penguin.linknote.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserSignInCommand {
    @Email
    private String email;
    @NotEmpty
    private String password;
}
