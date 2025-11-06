package com.penguin.linknote.domain.operations;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OperationsCreateCommand {
    @NotEmpty
    private String title;
}
