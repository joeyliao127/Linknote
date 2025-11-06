package com.penguin.linknote.domain.accesspolicies;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccessPoliciesCreateCommand {
    
    @NotNull
    private String title;
}
