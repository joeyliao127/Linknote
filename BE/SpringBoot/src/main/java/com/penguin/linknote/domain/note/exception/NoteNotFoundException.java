package com.penguin.linknote.domain.note.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException() {
    }
    public NoteNotFoundException(String message) {
        super(message);
    }
}
