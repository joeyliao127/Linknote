package com.penguin.linknote.domain.notebook.exception;

public class NotebookNotFoundException extends RuntimeException {
    public NotebookNotFoundException() {
        super("Notebook not found");
    }

    public NotebookNotFoundException(String message) {
        super(message);
    }
}
