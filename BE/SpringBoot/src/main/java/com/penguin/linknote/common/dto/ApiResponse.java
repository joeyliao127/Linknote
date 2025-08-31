package com.penguin.linknote.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ApiResponse {
    private Boolean result;
    private String message;
}
