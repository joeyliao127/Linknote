package com.penguin.linknote.domain.rbac;

import java.util.UUID;

public record ParsedResource(
    ResourceType resourceType,
    UUID targetId
) {}