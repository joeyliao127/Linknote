package com.penguin.linknote.domain.notebook.exception;

public class InvalidNotebookParameterException extends RuntimeException {
    public InvalidNotebookParameterException() {
        super("無效的參數");
    }

    public InvalidNotebookParameterException(String message) {
        super(message);
    }
}
