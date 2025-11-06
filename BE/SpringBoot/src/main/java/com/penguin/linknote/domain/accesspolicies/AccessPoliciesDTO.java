package com.penguin.linknote.domain.accesspolicies;

import java.time.Instant;
import java.util.List;

import com.penguin.linknote.entity.AccessPolicies;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccessPoliciesDTO {

    private int id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;

    public static AccessPoliciesDTO fromEntity(AccessPolicies accesspolicies) {
        return AccessPoliciesDTO.builder()
            .id(accesspolicies.getId())
            .title(accesspolicies.getTitle())
            .createdAt(accesspolicies.getCreatedAt())
            .updatedAt(accesspolicies.getUpdatedAt())
            .build();
    }

    public static List<AccessPoliciesDTO> fromEntityList(List<AccessPolicies> accesspoliciesList) {
        return accesspoliciesList.stream().map(AccessPoliciesDTO::fromEntity).toList();
    }
}
