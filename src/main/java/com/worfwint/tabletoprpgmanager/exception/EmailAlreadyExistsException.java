package com.worfwint.tabletoprpgmanager.exception;

public class EmailAlreadyExistsException extends BadRequestException {

    public EmailAlreadyExistsException() {
        super("Email already exists");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
