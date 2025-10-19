package com.penguin.linknote.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageCommand {
    private Integer pageSize;
    private Integer page;
}
