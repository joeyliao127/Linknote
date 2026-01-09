package com.penguin.linknote.domain.tag.exception;

public class InvalidTagParameterException extends RuntimeException {
    public InvalidTagParameterException() {
        super("無效的參數");
    }

    public InvalidTagParameterException(String message) {
        super(message);
    }
}
