package com.penguin.linknote.domain.tag.exception;

import com.penguin.linknote.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.penguin.linknote.controller.tag")
public class TagExceptionHandler {

    @ExceptionHandler(InvalidTagParameterException.class)
    public ResponseEntity<ApiResponse> handleInvalidParameter(InvalidTagParameterException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTagNotFound(TagNotFoundException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
