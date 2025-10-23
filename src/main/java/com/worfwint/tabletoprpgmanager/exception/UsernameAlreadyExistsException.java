package com.worfwint.tabletoprpgmanager.exception;

public class UsernameAlreadyExistsException extends BadRequestException {

    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
