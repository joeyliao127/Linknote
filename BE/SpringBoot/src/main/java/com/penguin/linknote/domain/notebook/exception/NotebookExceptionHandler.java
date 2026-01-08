package com.penguin.linknote.domain.notebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.penguin.linknote.common.dto.ApiResponse;

@RestControllerAdvice(basePackages = "com.penguin.linknote.controller.notebook")
public class NotebookExceptionHandler {
    @ExceptionHandler(InvalidNotebookParameterException.class)
    public ResponseEntity<ApiResponse> handleInvalidOrder(InvalidNotebookParameterException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(NotebookNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotebookNotFound(NotebookNotFoundException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
