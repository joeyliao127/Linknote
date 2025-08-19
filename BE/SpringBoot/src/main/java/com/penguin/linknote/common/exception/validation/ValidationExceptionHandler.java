package com.penguin.linknote.common.exception.validation;

import com.penguin.linknote.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        ApiResponse apiResponse = new ApiResponse(false, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // @NotNull, @Max, @Min... 拋出的異常
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleValidationException(ConstraintViolationException e) {
        ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
