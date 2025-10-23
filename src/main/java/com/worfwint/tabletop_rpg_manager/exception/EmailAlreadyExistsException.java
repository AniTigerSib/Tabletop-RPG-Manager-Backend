package com.worfwint.tabletop_rpg_manager.exception;

public class EmailAlreadyExistsException extends BadRequestException {

    public EmailAlreadyExistsException() {
        super("Email already exists");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
