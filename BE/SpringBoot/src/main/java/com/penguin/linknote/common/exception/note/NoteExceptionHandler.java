package com.penguin.linknote.common.exception.note;

import com.penguin.linknote.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.penguin.linknote.controller.note")
public class NoteExceptionHandler {

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNoteNotFoundException(NoteNotFoundException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
