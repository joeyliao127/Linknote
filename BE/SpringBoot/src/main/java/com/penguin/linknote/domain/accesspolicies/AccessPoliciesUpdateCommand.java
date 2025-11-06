package com.penguin.linknote.domain.accesspolicies;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AccessPoliciesUpdateCommand {

    @NotEmpty
    private String title;
}
