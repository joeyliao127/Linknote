package com.penguin.linknote.common.exception.note;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException() {
    }
    public NoteNotFoundException(String message) {
        super(message);
    }
}
