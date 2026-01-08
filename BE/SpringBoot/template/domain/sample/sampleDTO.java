package com.penguin.linknote.domain.!{lower};

import com.penguin.linknote.entity.!{upper};
import lombok.Builder;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class !{upper}DTO {
	//TODO: 修改 fields
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    //TODO: 修改 fromEntity methods
    public static !{upper}DTO fromEntity(!{upper} !{lower}) {
        return !{upper}DTO.builder()
                .id(!{lower}.getId())
                .createdAt(!{lower}.getCreatedAt())
                .updatedAt(!{lower}.getUpdatedAt())
                .build();
    }

    public static List<!{upper}DTO> fromEntityList(List<!{upper}> !{lower}List) {
        return !{lower}List.stream().map(!{upper}DTO::fromEntity).toList();
    }
}
