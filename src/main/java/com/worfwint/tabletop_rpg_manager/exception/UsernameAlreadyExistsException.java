package com.worfwint.tabletop_rpg_manager.exception;

public class UsernameAlreadyExistsException extends BadRequestException {

    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
