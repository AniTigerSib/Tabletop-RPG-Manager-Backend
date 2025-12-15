package com.worfwint.tabletoprpgmanager.auth.exception;

import com.worfwint.tabletoprpgmanager.common.exception.BadRequestException;

/**
 * Indicates that a registration request attempted to use a username that is already taken.
 */
public class UsernameAlreadyExistsException extends BadRequestException {

    /**
     * Creates the exception with a default message explaining the conflict.
     */
    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }

    /**
     * Creates the exception with a custom message.
     *
     * @param message custom description of the username conflict
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
