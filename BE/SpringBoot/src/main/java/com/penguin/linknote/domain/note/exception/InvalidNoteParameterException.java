package com.penguin.linknote.domain.note.exception;

public class InvalidNoteParameterException extends RuntimeException {
    public InvalidNoteParameterException() {
        super("無效的參數");
    }

    public InvalidNoteParameterException(String message) {
        super(message);
    }
}
