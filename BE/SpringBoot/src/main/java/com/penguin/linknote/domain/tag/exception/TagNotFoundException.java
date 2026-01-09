package com.penguin.linknote.domain.tag.exception;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException() {
        super("Tag not found");
    }

    public TagNotFoundException(String message) {
        super(message);
    }
}
