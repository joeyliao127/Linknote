package com.penguin.linknote.domain.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserChangePasswordCommand {
    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;
}
